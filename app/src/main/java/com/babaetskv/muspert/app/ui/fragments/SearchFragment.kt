package com.babaetskv.muspert.app.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentSearchBinding
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.app.presentation.search.SearchPresenter
import com.babaetskv.muspert.app.presentation.search.SearchView
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.ui.item.AlbumItem
import com.babaetskv.muspert.app.utils.setGone
import com.babaetskv.muspert.app.utils.setVisible
import com.babaetskv.muspert.app.utils.viewBinding
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

/**
 * @author Konstantin on 13.05.2020
 */
class SearchFragment : BaseFragment(), SearchView {
    private val presenter: SearchPresenter by moxyPresenter {
        SearchPresenter(get(), get(), get(), get(), get())
    }
    private val binding: FragmentSearchBinding by viewBinding()
    private lateinit var adapter: FastAdapter<AlbumItem>
    private lateinit var itemAdapter: ItemAdapter<AlbumItem>

    override val layoutResId: Int
        get() = R.layout.fragment_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListeners()
    }

    override fun showProgress() {
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    override fun showPresearchView(show: Boolean) {
        with (binding.emptyViewSearch) {
            if (show) {
                setBanner(R.drawable.ic_search)
                setTitle(R.string.presearch_title)
                setMessage(null)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(null)
                setVisible()
            } else setGone()
        }
    }

    override fun showEmptyView(show: Boolean) {
        with (binding.emptyViewSearch) {
            if (show) {
                setBanner(R.drawable.ic_empty_list)
                setTitle(R.string.empty_search_results_title)
                setMessage(R.string.empty_search_results_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(null)
                setVisible()
            } else setGone()
        }
    }

    override fun showErrorView(show: Boolean) {
        with (binding.emptyViewSearch) {
            if (show) {
                setBanner(R.drawable.ic_error)
                setTitle(R.string.empty_album_list_title)
                setMessage(R.string.error_albums_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.refreshSearchResults()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun populateAlbums(albums: List<Album>) {
        itemAdapter.setNewList(albums.map { AlbumItem(it) })
    }

    override fun populateQuery(query: String) {
        binding.etSearch.setText(query)
    }

    private fun initListeners() {
        binding.btnClear.setOnClickListener {
            presenter.onClear()
        }
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            presenter.onSearchQueryChanged(text.toString())
        }
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.refreshSearchResults()
                true
            }
            false
        }
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        adapter = FastAdapter.with(itemAdapter).apply {
            setHasStableIds(true)
            val preloadMargin: Int = resources.getInteger(R.integer.preload_margin)
            onBindViewHolderListener = object : OnBindViewHolderListenerImpl<AlbumItem>() {

                override fun onBindViewHolder(
                    viewHolder: RecyclerView.ViewHolder,
                    position: Int,
                    payloads: List<Any>
                ) {
                    super.onBindViewHolder(viewHolder, position, payloads)
                    if (position == adapter.itemCount - preloadMargin) {
                        binding.recyclerResults.post {
                            presenter.loadNextPage()
                        }
                    }
                }
            }
            onClickListener = object : ClickListener<AlbumItem> {

                override fun invoke(
                    v: View?,
                    adapter: IAdapter<AlbumItem>,
                    item: AlbumItem,
                    position: Int
                ): Boolean = item.album?.let {
                    presenter.onSelectAlbum(item.album)
                    return true
                } ?: false
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerResults.apply {
            val orientation: Int
            layoutManager = LinearLayoutManager(requireContext()).also {
                orientation = it.orientation
            }
            adapter = this@SearchFragment.adapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, orientation).apply {
                val color = ContextCompat.getColor(requireContext(), R.color.colorHint)
                setBackgroundColor(color)
            })
        }
    }
}
