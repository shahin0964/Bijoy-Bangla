package com.bijoy.bangla.engine

enum class KeyIconType {
    NONE, SHIFT, BACKSPACE, GLOBE, RETURN, SYMBOLS, SPACE, EMOJI
}

data class KeyModel(
    val primaryText: String,
    val shiftedText: String = "",
    val hintText: String = "",
    val isSpecial: Boolean = false,
    val widthWeight: Float = 1.0f,
    val iconType: KeyIconType = KeyIconType.NONE
)

object LayoutDefinitions {

    private val topRowNumbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private val topRowBanglaNumbers = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০")

    // 1. English QWERTY with Gboard-style top row number hints
    val englishRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").mapIndexed { index, char ->
        KeyModel(primaryText = char, shiftedText = char.uppercase(), hintText = topRowNumbers[index])
    }
    val englishRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").map {
        KeyModel(primaryText = it, shiftedText = it.uppercase())
    }
    val englishRow3 = listOf(
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.SHIFT),
        KeyModel("z", "Z"),
        KeyModel("x", "X"),
        KeyModel("c", "C"),
        KeyModel("v", "V"),
        KeyModel("b", "B"),
        KeyModel("n", "N"),
        KeyModel("m", "M"),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 2. Avro Phonetic with top row number hints
    val avroRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").mapIndexed { index, char ->
        KeyModel(primaryText = char, shiftedText = char.uppercase(), hintText = topRowBanglaNumbers[index])
    }
    val avroRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").map {
        KeyModel(primaryText = it, shiftedText = it.uppercase())
    }
    val avroRow3 = listOf(
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.SHIFT),
        KeyModel("z", "Z"), KeyModel("x", "X"), KeyModel("c", "C"), KeyModel("v", "V"),
        KeyModel("b", "B"), KeyModel("n", "N"), KeyModel("m", "M"),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 3. Bijoy PC Layout (Direct Bangla Letters according to Bijoy 52 Mapping) with hints
    val bijoyRow1 = listOf(
        KeyModel("ঙ", "ং", "১"), KeyModel("য", "য়", "২"), KeyModel("ড", "ঢ", "৩"), KeyModel("প", "ফ", "৪"), KeyModel("ট", "ঠ", "৫"),
        KeyModel("চ", "ছ", "৬"), KeyModel("জ", "ঝ", "৭"), KeyModel("হ", "ঞ", "৮"), KeyModel("গ", "ঘ", "৯"), KeyModel("ড়", "ঢ়", "০")
    )
    val bijoyRow2 = listOf(
        KeyModel("ৃ", "ঋ"), KeyModel("ু", "ূ"), KeyModel("ি", "ী"), KeyModel("া", "াঁ"), KeyModel("্", "।"),
        KeyModel("ব", "ভ"), KeyModel("ক", "খ"), KeyModel("ত", "থ"), KeyModel("দ", "ধ")
    )
    val bijoyRow3 = listOf(
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.SHIFT),
        KeyModel("্র", "্য"), KeyModel("ও", "ৌ"), KeyModel("ে", "ৈ"), KeyModel("র", "ল"),
        KeyModel("ন", "ণ"), KeyModel("স", "ষ"), KeyModel("ম", "শ"),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 4. Smart Mobile Bangla Layout
    val smartRow1 = listOf(
        KeyModel("ক", "অ", "১"), KeyModel("খ", "আ", "২"), KeyModel("গ", "ই", "৩"), KeyModel("ঘ", "ঈ", "৪"), KeyModel("ঙ", "উ", "৫"),
        KeyModel("চ", "ঊ", "৬"), KeyModel("ছ", "ঋ", "৭"), KeyModel("জ", "এ", "৮"), KeyModel("ঝ", "ঐ", "৯"), KeyModel("ঞ", "ও", "০")
    )
    val smartRow2 = listOf(
        KeyModel("ট", "া"), KeyModel("ঠ", "ি"), KeyModel("ড", "ী"), KeyModel("ঢ", "ু"), KeyModel("ণ", "ূ"),
        KeyModel("ত", "ে"), KeyModel("থ", "ৈ"), KeyModel("দ", "ো"), KeyModel("ধ", "ৌ"), KeyModel("ন", "ৃ")
    )
    val smartRow3 = listOf(
        KeyModel("", isSpecial = true, widthWeight = 1.2f, iconType = KeyIconType.SHIFT),
        KeyModel("প", "্"), KeyModel("ফ", "ৎ"), KeyModel("ব", "ং"), KeyModel("ভ", "ঃ"),
        KeyModel("ম", "ঁ"), KeyModel("য", "্য"), KeyModel("র", "্র"), KeyModel("ল", "।"),
        KeyModel("শ", "স"), KeyModel("হ", "ড়"),
        KeyModel("", isSpecial = true, widthWeight = 1.2f, iconType = KeyIconType.BACKSPACE)
    )

    // 5. English Symbols (123)
    val symbolsEnRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { KeyModel(primaryText = it) }
    val symbolsEnRow2 = listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\"").map { KeyModel(primaryText = it) }
    val symbolsEnRow3 = listOf(
        KeyModel("#+=", isSpecial = true, widthWeight = 1.35f),
        KeyModel(".", ""), KeyModel(",", ""), KeyModel("?", ""), KeyModel("!", ""),
        KeyModel("'", ""), KeyModel("%", ""), KeyModel("*", ""),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 6. Bangla Symbols & Digits (১২৩)
    val symbolsBnRow1 = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০").map { KeyModel(primaryText = it) }
    val symbolsBnRow2 = listOf("৳", "।", "‘", "’", "“", "”", "(", ")", "—", "-").map { KeyModel(primaryText = it) }
    val symbolsBnRow3 = listOf(
        KeyModel("123", isSpecial = true, widthWeight = 1.35f),
        KeyModel(",", ""), KeyModel("?", ""), KeyModel("!", ""), KeyModel(":", ""),
        KeyModel(";", ""), KeyModel("/", ""), KeyModel("%", ""),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // Popular Emojis
    val popularEmojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "🥹", "😊", "😇",
        "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
        "😜", "🤪", "😝", "🤑", "🤗", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨",
        "😐", "😑", "😶", "🫥", "😏", "😒", "🙄", "😬", "😮‍💨", "🤥", "😌", "😔",
        "❤️", "🩷", "🧡", "💛", "💚", "💙", "🩵", "💜", "🖤", "🩶", "🤍", "🤎",
        "💔", "❤️‍🔥", "❤️‍🩹", "💯", "🔥", "✨", "🎉", "👏", "👍", "👎", "🫶", "🙏",
        "🇧🇩", "🤝", "💪", "💐", "🌸", "🌹", "☕", "🍕", "📱", "💻", "🚀", "⚡"
    )
}
