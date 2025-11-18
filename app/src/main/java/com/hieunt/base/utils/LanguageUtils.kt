package com.hieunt.base.utils

import com.hieunt.base.R
import com.hieunt.base.domain.model.LanguageParentModel
import com.hieunt.base.domain.model.LanguageSubModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageUtils @Inject constructor() {
    fun getAllLanguages(): List<LanguageParentModel> {
        val lists: MutableList<LanguageParentModel> = ArrayList()
        lists.add(
            LanguageParentModel(
                "Hindi", "hi", false, R.drawable.ic_hindi_flag,
                listOf(
                    LanguageSubModel(
                        R.drawable.flag_hindi_india,
                        "Hindi (Standard – India)",
                        "hi",
                        false
                    ),
                    LanguageSubModel(R.drawable.flag_hindi_el, "Hindi (Hinglish)", "hi", false)
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "Spanish", "es", false, R.drawable.ic_span_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_spain_spain, "Spanish (Spain)", "es", false),
                    LanguageSubModel(
                        R.drawable.flag_spain_latin,
                        "Spanish (Latin America)",
                        "es",
                        false
                    ),
                    LanguageSubModel(R.drawable.flag_spain_mexico, "Spanish (Mexico)", "es", false)
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "French", "fr", false, R.drawable.ic_french_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_fr_fr, "French (France)", "fr", false),
                    LanguageSubModel(R.drawable.flag_fr_canada, "French (Canada)", "fr", false),
                    LanguageSubModel(R.drawable.flag_fr_afica, "French (Africa)", "fr", false)
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "English", "en", false, R.drawable.ic_english_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_el_uk, "English (UK)", "en", false),
                    LanguageSubModel(R.drawable.flag_el_us, "English (US)", "en", false),
                    LanguageSubModel(R.drawable.flag_el_india, "English (India)", "en", false),
                    LanguageSubModel(
                        R.drawable.flag_el_international,
                        "English (International)",
                        "en",
                        false
                    )
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "German", "de", false, R.drawable.ic_german_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_de_de, "German (Germany)", "de", false),
                    LanguageSubModel(R.drawable.flag_de_austria, "German (Austria)", "de", false),
                    LanguageSubModel(
                        R.drawable.flag_de_switzer,
                        "German (Switzerland)",
                        "de",
                        false
                    )
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "Indonesian", "in", false, R.drawable.ic_indo_flag,
                listOf(
                    LanguageSubModel(
                        R.drawable.flag_indo_spain,
                        "Indonesian (Standard)",
                        "in",
                        false
                    ),
                    LanguageSubModel(
                        R.drawable.flag_indo_spain,
                        "Indonesian (Informal, English combined)",
                        "in",
                        false
                    ),
                    LanguageSubModel(
                        R.drawable.flag_indo_japan,
                        "Indonesian (Japanese-influenced)",
                        "in",
                        false
                    )
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "Portuguese", "pt", false, R.drawable.ic_portuguese_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_pt_pt, "Portuguese (Portugal)", "pt", false),
                    LanguageSubModel(R.drawable.flag_pt_brazil, "Portuguese (Brazil)", "pt", false),
                    LanguageSubModel(R.drawable.flag_pt_afica, "Portuguese (Africa)", "pt", false)
                )
            )
        )

        lists.add(
            LanguageParentModel(
                "Chinese", "zh", false, R.drawable.ic_china_flag,

                listOf(
                    LanguageSubModel(R.drawable.flag_cn_cn, "Chinese (China)", "zh", false),
                    LanguageSubModel(R.drawable.flag_cn_kh, "Chinese (Hong Kong)", "zh", false),
                    LanguageSubModel(R.drawable.flag_cn_tw, "Chinese (Taiwan)", "zh", false)
                )
            )
        )

        lists.add(
            LanguageParentModel(
                "Swahili", "sw", false, R.drawable.ic_swahili_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Korean", "ko", false, R.drawable.ic_korean_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Rusian", "ru", false, R.drawable.ic_russia_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Turkish", "tr", false, R.drawable.ic_turkey_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Arabic", "ar", false, R.drawable.ic_a_rap_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Amharic", "am", false, R.drawable.ic_amharic_flag,
                listOf()
            )
        )
        lists.add(
            LanguageParentModel(
                "Zulu", "zu", false, R.drawable.ic_zulu_flag,
                listOf(
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Standard)", "zu", false),
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Urban)", "zu", false),
                    LanguageSubModel(R.drawable.flag_zulu_zulu, "Zulu (Traditional)", "zu", false)
                )
            )
        )
        lists.add(
            LanguageParentModel(
                "Yoruba", "yo", false, R.drawable.ic_yoruba_flag,
                listOf()
            )
        )

        lists.add(
            LanguageParentModel(
                "Afrikaans", "af", false, R.drawable.ic_afrikaans_flag,
                listOf(
                    LanguageSubModel(
                        R.drawable.flag_standard,
                        "Afrikaans (South Africa – Standard)",
                        "af",
                        false
                    ),
                    LanguageSubModel(R.drawable.flag_standard, "Afrikaans (Informal)", "af", false),
                    LanguageSubModel(
                        R.drawable.flag_standard_namibia,
                        "Afrikaans (Namibia)",
                        "af",
                        false
                    )
                )
            )
        )

        return lists
    }

}