package com.bijoy.bangla.engine

class BijoyEngine {

    companion object {
        // Normal Bijoy key mapping (unshifted)
        val normalMap = mapOf(
            'q' to "ঙ", 'w' to "য", 'e' to "ড", 'r' to "প", 't' to "ট",
            'y' to "চ", 'u' to "জ", 'i' to "হ", 'o' to "গ", 'p' to "ড়",
            'a' to "ৃ", 's' to "ু", 'd' to "ি", 'f' to "া", 'g' to "্",
            'h' to "ব", 'j' to "ক", 'k' to "ত", 'l' to "দ",
            'z' to "্র", 'x' to "ও", 'c' to "ে", 'v' to "র", 'b' to "ন",
            'n' to "স", 'm' to "ম"
        )

        // Shifted Bijoy key mapping
        val shiftMap = mapOf(
            'Q' to "ং", 'W' to "য়", 'E' to "ঢ", 'R' to "ফ", 'T' to "ঠ",
            'Y' to "ছ", 'U' to "ঝ", 'I' to "ঞ", 'O' to "ঘ", 'P' to "ঢ়",
            'A' to "ঋ", 'S' to "ূ", 'D' to "ী", 'F' to "াঁ", 'G' to "।",
            'H' to "ভ", 'J' to "খ", 'K' to "থ", 'L' to "ধ",
            'Z' to "্য", 'X' to "ৌ", 'C' to "ৈ", 'V' to "ল", 'B' to "ণ",
            'N' to "ষ", 'M' to "শ"
        )

        // Link key (g = ্) combinations for standalone vowels
        val vowelWithLink = mapOf(
            "া" to "অ",
            "ি" to "ই",
            "ী" to "ঈ",
            "ু" to "উ",
            "ূ" to "ঊ",
            "ৃ" to "ঋ",
            "ে" to "এ",
            "ৈ" to "ঐ",
            "ও" to "ও",
            "ৌ" to "ঔ"
        )
    }

    private var lastWasLinkKey = false

    fun processKey(char: Char, isShifted: Boolean): Pair<String, Boolean> {
        val mapped = if (isShifted) {
            shiftMap[char] ?: char.toString()
        } else {
            normalMap[char.lowercaseChar()] ?: char.toString()
        }

        if (lastWasLinkKey) {
            lastWasLinkKey = false
            val combinedVowel = vowelWithLink[mapped]
            if (combinedVowel != null) {
                // Replace link key with standalone vowel
                return Pair(combinedVowel, true) // true indicates replace previous
            }
        }

        if (mapped == "্") {
            lastWasLinkKey = true
        } else {
            lastWasLinkKey = false
        }

        return Pair(mapped, false)
    }

    fun resetState() {
        lastWasLinkKey = false
    }
}
