package io.github.alxiw.punkbrew.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

abstract class BaseFragment<VM : BaseViewModel>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initView(view)
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> onLoading()
                        is UiState.Content -> onContentReceived()
                        is UiState.Empty -> onEmptyContent()
                        is UiState.Error -> onError(state.message)
                    }
                }
            }
        }
    }

    abstract fun setupToolbar()

    abstract fun initView(view: View)

    abstract fun onLoading()

    abstract fun onContentReceived()

    abstract fun onEmptyContent()

    open fun onError(message: String?) {}
}
