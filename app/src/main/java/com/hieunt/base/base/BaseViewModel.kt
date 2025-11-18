package com.hieunt.base.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hieunt.base.state.ErrorsState
import com.hieunt.base.state.LoadingState
import com.hieunt.base.state.UiStateStore
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel<S : Any> : ViewModel() {
    abstract fun initState(): S

    val uiStateStore by lazy { UiStateStore(this.initState()) }

    val currentState: S get() = uiStateStore.currentUiState

    val errorsState by lazy { ErrorsState() }

    val loadingState by lazy { LoadingState() }

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("coroutineException1901", "${exception.message}")
    }

    protected fun dispatchStateUi(uiState: S) {
        this.uiStateStore.updateStateUi(uiState = uiState)
    }

    protected fun dispatchStateError(error: Throwable) {
        errorsState.emitError(error)
    }

    protected fun dispatchStateLoading(isShowLoading: Boolean){
        loadingState.updateLoadingState(isShowLoading)
    }
}