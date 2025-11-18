package com.hieunt.base.presentations.feature.screen_base.language_start

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hieunt.base.R
import com.hieunt.base.base.BaseViewModel
import com.hieunt.base.di.IoDispatcher
import com.hieunt.base.domain.model.LanguageModelNew
import com.hieunt.base.utils.SystemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageStartViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
) : BaseViewModel<LanguageUiState>() {
    override fun initState(): LanguageUiState = LanguageUiState.Idle

    fun initListLanguage() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            dispatchStateUi(LanguageUiState.Loading)
            val lists: MutableList<LanguageModelNew> = ArrayList()
            lists.add(LanguageModelNew("Español", "es", false, R.drawable.ic_span_flag))
            lists.add(LanguageModelNew("Français", "fr", false, R.drawable.ic_french_flag))
            lists.add(LanguageModelNew("हिन्दी", "hi", false, R.drawable.ic_hindi_flag))
            lists.add(LanguageModelNew("English", "en", false, R.drawable.ic_english_flag))
            lists.add(
                LanguageModelNew(
                    "Português (Brazil)",
                    "pt-rBR",
                    false,
                    R.drawable.ic_brazil_flag
                )
            )
            lists.add(
                LanguageModelNew(
                    "Português (Portu)",
                    "pt-rPT",
                    false,
                    R.drawable.ic_portuguese_flag
                )
            )
            lists.add(LanguageModelNew("日本語", "ja", false, R.drawable.ic_japan_flag))
            lists.add(LanguageModelNew("Deutsch", "de", false, R.drawable.ic_german_flag))
            lists.add(LanguageModelNew("中文 (简体)", "zh-rCN", false, R.drawable.ic_china_flag))
            lists.add(LanguageModelNew("中文 (繁體) ", "zh-rTW", false, R.drawable.ic_china_flag))
            lists.add(LanguageModelNew("عربي ", "ar", false, R.drawable.ic_a_rap_flag))
            lists.add(LanguageModelNew("বাংলা ", "bn", false, R.drawable.ic_bengali_flag))
            lists.add(LanguageModelNew("Русский ", "ru", false, R.drawable.ic_russia_flag))
            lists.add(LanguageModelNew("Türkçe ", "tr", false, R.drawable.ic_turkey_flag))
            lists.add(LanguageModelNew("한국인 ", "ko", false, R.drawable.ic_korean_flag))
            lists.add(LanguageModelNew("Indonesia", "in", false, R.drawable.flag_indonesia))

            val languagePre = SystemUtils.getPreLanguage(context)
            val selectedLang = lists.find { it.isoLanguage == languagePre }

            selectedLang?.let { lang ->
                lang.isCheck = true
                lists.remove(lang)
                lists.add(0, lang)
            }


            dispatchStateUi(LanguageUiState.Language(listLanguage = lists))
        }.invokeOnCompletion {
            if (it != null) dispatchStateUi(LanguageUiState.Error(it))
        }
    }

    fun setSelectLanguage(isoLanguage: String) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            when (val state = currentState) {
                is LanguageUiState.Language -> {
                    val listUpdate = state.listLanguage.map {
                        it.copy(isShowAnim = false, isCheck = it.isoLanguage == isoLanguage)
                    }
                    dispatchStateUi(state.copy(listLanguage = listUpdate))
                }

                else -> {
                    Log.w(
                        "LanguageVM",
                        "Ignoring setSelectLanguage because current state is not LanguageState"
                    )
                }
            }
        }
    }
}