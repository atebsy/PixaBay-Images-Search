package com.example.android.coding.challenge.ui.searchphotos

import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coding.challenge.DataStoreUtil
import com.example.android.coding.challenge.MainViewModel
import com.example.android.coding.challenge.R
import com.example.android.coding.challenge.SearchSuggestionProvider
import com.example.android.coding.challenge.databinding.FragmentSearchPhotosBinding
import com.example.android.coding.challenge.models.Photo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchPhotosFragment : Fragment() {

    private lateinit var binding: FragmentSearchPhotosBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            edtSearchPhoto.setOnClickListener {
                requireActivity().onSearchRequested()
            }

            bindState(
                uiState = viewModel.state,
                pagingData = viewModel.pagingDataFlow,
                uiActions = viewModel.accept
            )

            binImage.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                    .setMessage(getString(R.string.delete_search))
                    .setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
                        SearchRecentSuggestions(
                            requireContext(),
                            SearchSuggestionProvider.AUTHORITY,
                            SearchSuggestionProvider.MODE
                        ).clearHistory()
                        viewModel.setHasSuggestions(false)
                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                    .show()

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchString
                    .collect {
                        Log.d("TAG", "search string: ${it}")
                        binding.apply {
                            edtSearchPhoto.setText(it)
                            updateSearchListFromInput(viewModel.accept)
                        }
                    }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.hasSuggestion.collect {
                    Log.d("TAG", "visibility: ${it}")
                    when (it) {
                        false -> binding.binImage.visibility = View.GONE
                        else -> binding.binImage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    /**
     * Binds the [UiState] provided  by the [MainViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentSearchPhotosBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        uiActions: (UiAction) -> Unit
    ) {
        val photosAdapter = SearchPhotosAdapter {
            val action = SearchPhotosFragmentDirections
                .actionSearchPhotosFragmentToPhotosDetailFragment(
                    photo = it
                )
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                .setMessage(getString(R.string.see_details))
                .setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
                    view?.findNavController()?.navigate(action)
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .show()
        }
        val header = PhotosLoadStateAdapter { photosAdapter.retry() }
        list.adapter = photosAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = PhotosLoadStateAdapter { photosAdapter.retry() }
        )
        bindList(
            header = header,
            photoAdapter = photosAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentSearchPhotosBinding.updateSearchListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        edtSearchPhoto.text.let {
            if (it.isNotEmpty()) {
                //list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentSearchPhotosBinding.bindList(
        header: PhotosLoadStateAdapter,
        photoAdapter: SearchPhotosAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { photoAdapter.retry() }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = photoAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        viewLifecycleOwner.lifecycleScope.launch {
            pagingData.collectLatest { pagingData ->
                photoAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            photoAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && photoAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && photoAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                list.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && photoAdapter.itemCount == 0
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireActivity(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }
}
