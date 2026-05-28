package io.github.alxiw.punkbrew.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    abstract val viewModel: VM

    abstract val layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setupToolbar()
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> onLoading()
                is UiState.Content -> onContentReceived()
                is UiState.Empty -> onEmptyContent()
                is UiState.Error -> onError(state.message)
            }
        })
    }

    abstract fun setupToolbar()

    abstract fun initView(view: View)

    abstract fun onLoading()

    abstract fun onContentReceived()

    abstract fun onEmptyContent()

    open fun onError(message: String?) {}
}
