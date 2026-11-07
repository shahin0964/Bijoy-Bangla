package com.bijoy.bangla.engine

enum class KeyIconType {
    NONE, SHIFT, BACKSPACE, GLOBE, RETURN, SYMBOLS, SPACE, EMOJI
}

data class KeyModel(
    val primaryText: String,
    val shiftedText: String = "",
    val isSpecial: Boolean = false,
    val widthWeight: Float = 1.0f,
    val iconType: KeyIconType = KeyIconType.NONE
)

object LayoutDefinitions {

    // 1. English QWERTY
    val englishRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").map { KeyModel(it, it.uppercase()) }
    val englishRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").map { KeyModel(it, it.uppercase()) }
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

    // 2. Avro Phonetic (QWERTY letters with phonetic hints)
    val avroRow1 = listOf(
        KeyModel("q", "Q"), KeyModel("w", "W"), KeyModel("e", "E"), KeyModel("r", "R"), KeyModel("t", "T"),
        KeyModel("y", "Y"), KeyModel("u", "U"), KeyModel("i", "I"), KeyModel("o", "O"), KeyModel("p", "P")
    )
    val avroRow2 = listOf(
        KeyModel("a", "A"), KeyModel("s", "S"), KeyModel("d", "D"), KeyModel("f", "F"), KeyModel("g", "G"),
        KeyModel("h", "H"), KeyModel("j", "J"), KeyModel("k", "K"), KeyModel("l", "L")
    )
    val avroRow3 = listOf(
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.SHIFT),
        KeyModel("z", "Z"), KeyModel("x", "X"), KeyModel("c", "C"), KeyModel("v", "V"),
        KeyModel("b", "B"), KeyModel("n", "N"), KeyModel("m", "M"),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 3. Bijoy PC Layout (Direct Bangla Letters according to Bijoy 52 Mapping)
    val bijoyRow1 = listOf(
        KeyModel("ঙ", "ং"), KeyModel("য", "য়"), KeyModel("ড", "ঢ"), KeyModel("প", "ফ"), KeyModel("ট", "ঠ"),
        KeyModel("চ", "ছ"), KeyModel("জ", "ঝ"), KeyModel("হ", "ঞ"), KeyModel("গ", "ঘ"), KeyModel("ড়", "ঢ়")
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

    // 4. Smart Mobile Bangla Layout (Intuitive, Touch-Friendly)
    val smartRow1 = listOf(
        KeyModel("ক", "অ"), KeyModel("খ", "আ"), KeyModel("গ", "ই"), KeyModel("ঘ", "ঈ"), KeyModel("ঙ", "উ"),
        KeyModel("চ", "ঊ"), KeyModel("ছ", "ঋ"), KeyModel("জ", "এ"), KeyModel("ঝ", "ঐ"), KeyModel("ঞ", "ও")
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
    val symbolsEnRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { KeyModel(it) }
    val symbolsEnRow2 = listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\"").map { KeyModel(it) }
    val symbolsEnRow3 = listOf(
        KeyModel("#+=", isSpecial = true, widthWeight = 1.35f),
        KeyModel(".", ""), KeyModel(",", ""), KeyModel("?", ""), KeyModel("!", ""),
        KeyModel("'", ""), KeyModel("%", ""), KeyModel("*", ""),
        KeyModel("", isSpecial = true, widthWeight = 1.35f, iconType = KeyIconType.BACKSPACE)
    )

    // 6. Bangla Symbols & Digits (১২৩)
    val symbolsBnRow1 = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০").map { KeyModel(it) }
    val symbolsBnRow2 = listOf("৳", "।", "‘", "’", "“", "”", "(", ")", "—", "-").map { KeyModel(it) }
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
