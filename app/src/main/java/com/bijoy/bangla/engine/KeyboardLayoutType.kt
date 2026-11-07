package com.bijoy.bangla.engine

enum class KeyboardLayoutType(val id: String, val displayName: String, val shortLabel: String) {
    ENGLISH("ENGLISH", "English (US)", "EN"),
    AVRO("AVRO", "অভ্র ফোনেটিক", "অভ্র"),
    BIJOY("BIJOY", "বিজয় পিসি", "বিজয়"),
    SMART_MOBILE("SMART_MOBILE", "সহজ বাংলা", "সহজ"),
    SYMBOLS_EN("SYMBOLS_EN", "Numbers & Symbols", "123"),
    SYMBOLS_BN("SYMBOLS_BN", "বাংলা সংখ্যা ও প্রতীক", "১২৩"),
    EMOJI("EMOJI", "Emoji", "😀");

    companion object {
        fun fromId(id: String): KeyboardLayoutType {
            return values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ENGLISH
        }
    }
}
