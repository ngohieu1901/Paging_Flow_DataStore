package com.hieunt.base.presentations.feature.screen_base.language_start_new

import android.content.Context
import com.hieunt.base.base.BaseViewModel
import com.hieunt.base.domain.model.LanguageParentModel
import com.hieunt.base.utils.LanguageUtils
import com.hieunt.base.utils.SystemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LanguageStartNewViewModel @Inject constructor(
    private val languageUtils: LanguageUtils,
    @ApplicationContext private val context: Context
): BaseViewModel<LanguageStartNewUiState>() {
    override fun initState(): LanguageStartNewUiState = LanguageStartNewUiState()

    fun initLanguagesStart() {
        val listLanguageStart = languageUtils.getAllLanguages()
        dispatchStateUi(currentState.copy(listLanguage = listLanguageStart))
    }

    fun initLanguagesSetting() {
        val languageNameSelected = SystemUtils.getPreLanguageName(context)

        val listLanguageSetting = languageUtils.getAllLanguages().map { languageParent ->
            languageParent.copy(
                isCheck = languageParent.languageName == languageNameSelected,
                listLanguageSubModel = languageParent.listLanguageSubModel.map { languageSub ->
                    languageSub.copy(isCheck = languageSub.languageName == languageNameSelected)
                })
        }.toMutableList()

        val languageSelected = listLanguageSetting.firstOrNull { it.isCheck || it.listLanguageSubModel.any { langSub -> langSub.isCheck } }
        languageSelected?.let {
            listLanguageSetting.remove(it)
            listLanguageSetting.add(0, it.copy(isExpand = true))
        }

        dispatchStateUi(currentState.copy(listLanguage = listLanguageSetting))
    }

    fun handleExpand(languageParent: LanguageParentModel) {
        val listUpdate = currentState.listLanguage.map {
            if (languageParent.languageName == it.languageName) it.copy(isExpand = !languageParent.isExpand) else it
        }
        dispatchStateUi(uiState = currentState.copy(listLanguage = listUpdate))
    }

    fun selectLanguage(languageName: String) {
        val listUpdate = currentState.listLanguage.map { languageParent ->
            languageParent.copy(
                isCheck = languageParent.languageName == languageName,
                listLanguageSubModel = languageParent.listLanguageSubModel.map { languageSub ->
                    languageSub.copy(isCheck = languageSub.languageName == languageName)
                })
        }
        dispatchStateUi(uiState = currentState.copy(listLanguage = listUpdate))
    }
}