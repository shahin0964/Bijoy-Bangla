package com.bijoy.bangla.engine

class AvroEngine {

    companion object {
        // High frequency word dictionary for instant accurate prediction
        private val dictionary = mapOf(
            "ami" to listOf("আমি", "আমী"),
            "tumi" to listOf("তুমি"),
            "apni" to listOf("আপনি"),
            "kemon" to listOf("কেমন"),
            "achho" to listOf("আছো"),
            "achen" to listOf("আছেন"),
            "bhalo" to listOf("ভালো", "ভাল"),
            "valo" to listOf("ভালো", "ভাল"),
            "bangla" to listOf("বাংলা"),
            "bangladesh" to listOf("বাংলাদেশ"),
            "dhaka" to listOf("ঢাকা"),
            "dhonnobad" to listOf("ধন্যবাদ"),
            "kotha" to listOf("কথা"),
            "ki" to listOf("কি", "কী"),
            "korcho" to listOf("করছো", "করছ"),
            "korchen" to listOf("করছেন"),
            "hobe" to listOf("হবে"),
            "jai" to listOf("যাই"),
            "khabar" to listOf("খাবার"),
            "valobashi" to listOf("ভালোবাসি"),
            "kichu" to listOf("কিছু"),
            "bolo" to listOf("বলো", "বল"),
            "kintu" to listOf("কিন্তু"),
            "bondhu" to listOf("বন্ধু"),
            "shomoy" to listOf("সময়"),
            "onek" to listOf("অনেক"),
            "shundor" to listOf("সুন্দর"),
            "bhai" to listOf("ভাই"),
            "apnar" to listOf("আপনার"),
            "tomar" to listOf("তোমার"),
            "amar" to listOf("আমার"),
            "desh" to listOf("দেশ"),
            "shob" to listOf("সব"),
            "shobaike" to listOf("সবাইকে"),
            "thik" to listOf("ঠিক"),
            "ekhon" to listOf("এখন"),
            "kobe" to listOf("কবে"),
            "kothay" to listOf("কোথায়"),
            "keno" to listOf("কেন"),
            "kaje" to listOf("কাজে"),
            "ghor" to listOf("ঘর"),
            "shokal" to listOf("সকাল"),
            "rat" to listOf("রাত")
        )

        // Conjuncts sorted by length descending
        private val conjuncts = listOf(
            "kkhm" to "ক্ষ্ম", "kkh" to "ক্ষ",
            "gdh" to "গ্ধ", "gn" to "গ্ন", "gm" to "গ্ম",
            "ngk" to "ঙ্ক", "ngkh" to "ঙ্খ", "ngg" to "ঙ্গ", "nggh" to "ঙ্ঘ",
            "cch" to "চ্ছ", "cc" to "চ্চ", "jjh" to "জ্ঝ", "jj" to "জ্জ", "jnh" to "জ্ঞ",
            "TT" to "ট্ট", "DD" to "ড্ড", "bDh" to "ব্ধ",
            "nt" to "ন্ত", "nth" to "ন্থ", "nd" to "ন্দ", "ndh" to "ন্ধ", "nn" to "ন্ন", "nm" to "ন্ম",
            "pt" to "প্ত", "pp" to "প্প", "bd" to "ব্দ", "bb" to "ব্ব",
            "mm" to "ম্ম", "mp" to "ম্প", "mph" to "ম্ফ", "mb" to "ম্ব", "mbh" to "ম্ভ",
            "shc" to "শ্চ", "shch" to "শ্ছ", "shn" to "শ্ন", "shb" to "শ্ব", "shm" to "শ্ম", "shl" to "শ্ল",
            "STh" to "ষ্ঠ", "ST" to "ষ্ট", "Sk" to "ষ্ক", "Sp" to "ষ্প", "Sph" to "ষ্ফ", "Sm" to "ষ্ম", "Sn" to "ষ্ণ",
            "skh" to "স্খ", "sk" to "স্ক", "sth" to "স্থ", "st" to "স্ত", "sph" to "স্ফ", "sp" to "স্প",
            "sn" to "স্ন", "sm" to "স্ম", "sl" to "স্ল", "sb" to "স্ব",
            "hn" to "হ্ন", "hm" to "হ্ম", "hl" to "হ্ল", "hy" to "হ্য",
            "kk" to "ক্ক", "kt" to "ক্ত", "kl" to "ক্ল", "kw" to "ক্ব",
            "tt" to "ত্ত", "tr" to "ত্র", "ty" to "ত্য", "tw" to "ত্ব",
            "dd" to "দ্দ", "ddh" to "দ্ধ", "dv" to "দ্ব", "db" to "দ্ব",
            "pr" to "প্র", "pl" to "প্ল", "ps" to "প্স",
            "br" to "ব্র", "bl" to "ব্ল",
            "bhr" to "ভ্র",
            "mr" to "ম্র", "ml" to "ম্ল",
            "sr" to "স্র",
            "kr" to "ক্র", "gr" to "গ্র", "dr" to "দ্র", "dhr" to "ধ্র", "nr" to "ন্র"
        )

        private val consonants = mapOf(
            "k" to "ক", "kh" to "খ", "g" to "গ", "gh" to "ঘ", "Ng" to "ঙ",
            "c" to "চ", "ch" to "চ", "Ch" to "ছ", "j" to "জ", "jh" to "ঝ", "NG" to "ঞ",
            "T" to "ট", "Th" to "ঠ", "D" to "ড", "Dh" to "ঢ", "N" to "ণ",
            "t" to "ত", "th" to "থ", "d" to "দ", "dh" to "ধ", "n" to "ন",
            "p" to "প", "f" to "ফ", "ph" to "ফ", "b" to "ব", "bh" to "ভ", "v" to "ভ", "m" to "ম",
            "z" to "য", "y" to "য়", "r" to "র", "l" to "ল",
            "sh" to "শ", "S" to "ষ", "Sh" to "ষ", "s" to "স", "h" to "হ",
            "R" to "ড়", "Rh" to "ঢ়", "y" to "য়"
        )

        private val independentVowels = mapOf(
            "a" to "আ", "A" to "আ", "aa" to "আ",
            "i" to "ই", "I" to "ঈ", "ee" to "ঈ",
            "u" to "উ", "U" to "ঊ", "oo" to "ঊ",
            "e" to "এ", "E" to "এ",
            "oi" to "ঐ",
            "o" to "ও", "O" to "ও",
            "ou" to "ঔ", "au" to "ঔ",
            "rri" to "ঋ"
        )

        private val dependentKars = mapOf(
            "a" to "া", "aa" to "া",
            "i" to "ি", "I" to "ী", "ee" to "ী",
            "u" to "ু", "U" to "ূ", "oo" to "ূ",
            "e" to "ে", "E" to "ে",
            "oi" to "ৈ",
            "o" to "ো", "O" to "ো",
            "ou" to "ৌ", "au" to "ৌ",
            "rri" to "ৃ"
        )
    }

    fun getCandidates(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        val lower = raw.lowercase()
        val list = mutableListOf<String>()

        // 1. Direct dictionary matches
        dictionary[lower]?.let { list.addAll(it) }

        // 2. Rule based transliteration
        val transliterated = parsePhonetic(raw)
        if (!list.contains(transliterated)) {
            list.add(0, transliterated)
        }

        return list.take(4)
    }

    fun parsePhonetic(input: String): String {
        if (input.isEmpty()) return ""
        val sb = StringBuilder()
        var i = 0
        var lastWasConsonant = false

        while (i < input.length) {
            val remaining = input.substring(i)

            // Special single modifiers
            if (remaining.startsWith("t`")) {
                sb.append("ৎ")
                i += 2
                lastWasConsonant = false
                continue
            }
            if (remaining.startsWith("ng")) {
                sb.append("ং")
                i += 2
                lastWasConsonant = false
                continue
            }
            if (remaining.startsWith(":")) {
                sb.append("ঃ")
                i += 1
                lastWasConsonant = false
                continue
            }
            if (remaining.startsWith("^")) {
                sb.append("ঁ")
                i += 1
                continue
            }

            // Check Ref (r + consonant)
            if (remaining.startsWith("r") && remaining.length > 1 && !isVowel(remaining[1]) && remaining[1] != 'r') {
                val nextChar = remaining.substring(1)
                // Find consonant after r
                var matchedConsonant: String? = null
                var matchLen = 0
                for ((consKey, consVal) in consonants) {
                    if (nextChar.startsWith(consKey) && consKey.length > matchLen) {
                        matchedConsonant = consVal
                        matchLen = consKey.length
                    }
                }
                if (matchedConsonant != null) {
                    sb.append(matchedConsonant).append("র্")
                    i += 1 + matchLen
                    lastWasConsonant = true
                    continue
                }
            }

            // Check Conjuncts
            var matchedConjunct: String? = null
            var conjunctLen = 0
            for ((key, value) in conjuncts) {
                if (remaining.startsWith(key) && key.length > conjunctLen) {
                    matchedConjunct = value
                    conjunctLen = key.length
                }
            }
            if (matchedConjunct != null) {
                sb.append(matchedConjunct)
                i += conjunctLen
                lastWasConsonant = true
                continue
            }

            // Check Consonants
            var matchedCons: String? = null
            var consLen = 0
            for ((key, value) in consonants) {
                if (remaining.startsWith(key) && key.length > consLen) {
                    matchedCons = value
                    consLen = key.length
                }
            }
            if (matchedCons != null) {
                // Check if followed by y (য-ফলা)
                val afterCons = remaining.substring(consLen)
                if (afterCons.startsWith("y") && afterCons.length > 1 && afterCons != "y") {
                    sb.append(matchedCons).append("্য")
                    i += consLen + 1
                    lastWasConsonant = true
                    continue
                }
                // Check if followed by r (র-ফলা)
                if (afterCons.startsWith("r") && afterCons.length > 1 && isVowel(afterCons[1])) {
                    sb.append(matchedCons).append("্র")
                    i += consLen + 1
                    lastWasConsonant = true
                    continue
                }

                sb.append(matchedCons)
                i += consLen
                lastWasConsonant = true
                continue
            }

            // Check Vowels
            if (lastWasConsonant) {
                var matchedKar: String? = null
                var karLen = 0
                for ((key, value) in dependentKars) {
                    if (remaining.startsWith(key) && key.length > karLen) {
                        matchedKar = value
                        karLen = key.length
                    }
                }
                if (matchedKar != null) {
                    sb.append(matchedKar)
                    i += karLen
                    lastWasConsonant = false
                    continue
                }
            }

            // Independent Vowels
            var matchedVowel: String? = null
            var vowelLen = 0
            for ((key, value) in independentVowels) {
                if (remaining.startsWith(key) && key.length > vowelLen) {
                    matchedVowel = value
                    vowelLen = key.length
                }
            }
            if (matchedVowel != null) {
                sb.append(matchedVowel)
                i += vowelLen
                lastWasConsonant = false
                continue
            }

            // Default fallback
            sb.append(remaining[0])
            i++
            lastWasConsonant = false
        }

        return sb.toString()
    }

    private fun isVowel(c: Char): Boolean {
        return c in "aeiouAEIOU"
    }
}
