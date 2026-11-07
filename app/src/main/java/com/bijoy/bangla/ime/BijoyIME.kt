package com.bijoy.bangla.ime

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bijoy.bangla.BijoyApp
import com.bijoy.bangla.data.ChatContextManager
import com.bijoy.bangla.data.HistoryDbHelper
import com.bijoy.bangla.engine.*
import com.bijoy.bangla.ui.MainActivity

class BijoyIME : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    // Typing Engines
    private val avroEngine = AvroEngine()
    private val bijoyEngine = BijoyEngine()
    private lateinit var feedbackHelper: FeedbackHelper

    // Observable Compose states
    private var currentLayoutState by mutableStateOf(KeyboardLayoutType.ENGLISH)
    private var isShiftedState by mutableStateOf(false)
    private var isCapsLockState by mutableStateOf(false)
    private var actionLabelState by mutableStateOf("return")
    private var avroBufferState by mutableStateOf("")
    private var avroCandidatesState by mutableStateOf<List<String>>(emptyList())
    private var activeContactState by mutableStateOf<String?>(null)

    // Current word sentence accumulator for History logging
    private val typedSentenceBuffer = StringBuilder()
    private var currentPackageName: String = ""

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        feedbackHelper = FeedbackHelper(this)
    }

    override fun onCreateInputView(): View {
        window.window?.let { win ->
            win.addFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
            win.decorView.let { decor ->
                decor.setViewTreeLifecycleOwner(this)
                decor.setViewTreeViewModelStoreOwner(this)
                decor.setViewTreeSavedStateRegistryOwner(this)
            }
        }

        return ComposeView(this).apply {
            setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@BijoyIME.lifecycle))
            setViewTreeLifecycleOwner(this@BijoyIME)
            setViewTreeViewModelStoreOwner(this@BijoyIME)
            setViewTreeSavedStateRegistryOwner(this@BijoyIME)

            setContent {
                KeyboardScreen(
                    currentLayout = currentLayoutState,
                    isShifted = isShiftedState,
                    isCapsLock = isCapsLockState,
                    actionLabel = actionLabelState,
                    avroBuffer = avroBufferState,
                    avroCandidates = avroCandidatesState,
                    activeContactName = activeContactState,
                    onKeyPressed = { key -> handleKeyPress(key) },
                    onCandidateSelected = { candidate -> commitAvroCandidate(candidate) },
                    onLayoutChanged = { layout -> switchLayout(layout) },
                    onClipboardPaste = { pasteClipboard() },
                    onOpenSettings = { openSettings() },
                    onVoiceInput = { triggerVoiceInput() }
                )
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        currentPackageName = info?.packageName ?: ""
        updateActionLabel(info)
        avroBufferState = ""
        avroCandidatesState = emptyList()
        bijoyEngine.resetState()

        // Smart Language Context Memory (Per-contact / Per-app)
        if (BijoyApp.prefs.isSmartChatMemoryEnabled) {
            val contact = BijoyApp.chatManager.getActiveKey()
            activeContactState = if (ChatContextManager.currentContact.isNotBlank()) {
                ChatContextManager.currentContact
            } else null

            val savedLayoutId = BijoyApp.chatManager.getSavedLayoutForActiveChat()
            if (!savedLayoutId.isNullOrBlank()) {
                currentLayoutState = KeyboardLayoutType.fromId(savedLayoutId)
            }
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        commitHistoryRecord()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    private fun handleKeyPress(key: KeyModel) {
        val ic = currentInputConnection ?: return
        feedbackHelper.playKeyClick(isSpecialKey = key.isSpecial)
        feedbackHelper.performHaptic()

        when (key.iconType) {
            KeyIconType.SHIFT -> {
                if (isShiftedState) {
                    if (!isCapsLockState) {
                        isCapsLockState = true
                    } else {
                        isShiftedState = false
                        isCapsLockState = false
                    }
                } else {
                    isShiftedState = true
                }
                return
            }
            KeyIconType.BACKSPACE -> {
                handleBackspace()
                return
            }
            KeyIconType.SPACE -> {
                handleSpace()
                return
            }
            KeyIconType.RETURN -> {
                handleReturn()
                return
            }
            KeyIconType.GLOBE, KeyIconType.SYMBOLS, KeyIconType.EMOJI -> {
                return
            }
            KeyIconType.NONE -> {
                // Character keys
                val rawText = if (isShiftedState && key.shiftedText.isNotEmpty()) {
                    key.shiftedText
                } else {
                    key.primaryText
                }

                when (currentLayoutState) {
                    KeyboardLayoutType.AVRO -> {
                        // Avro Phonetic Mode
                        avroBufferState += rawText
                        val candidates = avroEngine.getCandidates(avroBufferState)
                        avroCandidatesState = candidates
                        ic.setComposingText(avroEngine.parsePhonetic(avroBufferState), 1)
                    }
                    KeyboardLayoutType.BIJOY -> {
                        // Bijoy PC Mode
                        val char = rawText.firstOrNull() ?: return
                        val (output, replacePrevious) = bijoyEngine.processKey(char, isShiftedState)
                        if (replacePrevious) {
                            ic.deleteSurroundingText(1, 0)
                        }
                        ic.commitText(output, 1)
                        typedSentenceBuffer.append(output)
                    }
                    KeyboardLayoutType.SMART_MOBILE, KeyboardLayoutType.ENGLISH,
                    KeyboardLayoutType.SYMBOLS_EN, KeyboardLayoutType.SYMBOLS_BN,
                    KeyboardLayoutType.EMOJI -> {
                        ic.commitText(rawText, 1)
                        typedSentenceBuffer.append(rawText)
                    }
                }

                // Reset shift unless caps locked
                if (isShiftedState && !isCapsLockState) {
                    isShiftedState = false
                }
            }
        }
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return

        if (currentLayoutState == KeyboardLayoutType.AVRO && avroBufferState.isNotEmpty()) {
            avroBufferState = avroBufferState.dropLast(1)
            if (avroBufferState.isEmpty()) {
                avroCandidatesState = emptyList()
                ic.commitText("", 1)
            } else {
                avroCandidatesState = avroEngine.getCandidates(avroBufferState)
                ic.setComposingText(avroEngine.parsePhonetic(avroBufferState), 1)
            }
            return
        }

        ic.deleteSurroundingText(1, 0)
        if (typedSentenceBuffer.isNotEmpty()) {
            typedSentenceBuffer.deleteCharAt(typedSentenceBuffer.length - 1)
        }
    }

    private fun handleSpace() {
        val ic = currentInputConnection ?: return

        if (currentLayoutState == KeyboardLayoutType.AVRO && avroBufferState.isNotEmpty()) {
            val candidate = avroCandidatesState.firstOrNull() ?: avroEngine.parsePhonetic(avroBufferState)
            commitAvroCandidate(candidate)
        }

        ic.commitText(" ", 1)
        typedSentenceBuffer.append(" ")
    }

    private fun handleReturn() {
        val ic = currentInputConnection ?: return

        if (currentLayoutState == KeyboardLayoutType.AVRO && avroBufferState.isNotEmpty()) {
            val candidate = avroCandidatesState.firstOrNull() ?: avroEngine.parsePhonetic(avroBufferState)
            commitAvroCandidate(candidate)
        }

        commitHistoryRecord()

        val info = currentInputEditorInfo
        if (info != null && (info.imeOptions and EditorInfo.IME_MASK_ACTION) != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(info.imeOptions and EditorInfo.IME_MASK_ACTION)
        } else {
            ic.commitText("\n", 1)
        }
    }

    private fun commitAvroCandidate(candidate: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(candidate, 1)
        typedSentenceBuffer.append(candidate)
        avroBufferState = ""
        avroCandidatesState = emptyList()
    }

    private fun switchLayout(layout: KeyboardLayoutType) {
        currentLayoutState = layout
        BijoyApp.prefs.currentLayoutId = layout.id

        // Save layout for the active chat/contact if Smart Memory is enabled
        if (BijoyApp.prefs.isSmartChatMemoryEnabled) {
            BijoyApp.chatManager.saveLayoutForActiveChat(layout.id)
        }

        avroBufferState = ""
        avroCandidatesState = emptyList()
        bijoyEngine.resetState()
    }

    private fun pasteClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (!text.isNullOrBlank()) {
                currentInputConnection?.commitText(text, 1)
                typedSentenceBuffer.append(text)
                // Save to history as clipboard item
                val contact = ChatContextManager.currentContact.takeIf { it.isNotBlank() }
                BijoyApp.dbHelper.insertRecord(
                    content = text,
                    packageName = currentPackageName,
                    contactName = contact,
                    layoutUsed = currentLayoutState.shortLabel,
                    isClipboard = true
                )
            }
        }
    }

    private fun commitHistoryRecord() {
        val content = typedSentenceBuffer.toString().trim()
        if (content.length >= 2 && BijoyApp.prefs.isHistoryEnabled) {
            val isPassword = currentInputEditorInfo?.let {
                val type = it.inputType and EditorInfo.TYPE_MASK_VARIATION
                type == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                type == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                type == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
            } ?: false

            if (!isPassword) {
                val contact = ChatContextManager.currentContact.takeIf { it.isNotBlank() }
                BijoyApp.dbHelper.insertRecord(
                    content = content,
                    packageName = currentPackageName,
                    contactName = contact,
                    layoutUsed = currentLayoutState.shortLabel,
                    isClipboard = false
                )
            }
        }
        typedSentenceBuffer.clear()
    }

    private fun openSettings() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        startActivity(intent)
    }

    private fun updateActionLabel(info: EditorInfo?) {
        if (info == null) {
            actionLabelState = "return"
            return
        }
        val action = info.imeOptions and EditorInfo.IME_MASK_ACTION
        actionLabelState = when (action) {
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_SEARCH -> "Search"
            EditorInfo.IME_ACTION_SEND -> "Send"
            EditorInfo.IME_ACTION_NEXT -> "Next"
            EditorInfo.IME_ACTION_DONE -> "Done"
            else -> "return"
        }
    }

    private fun triggerVoiceInput() {
        try {
            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Voice typing service not available", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
