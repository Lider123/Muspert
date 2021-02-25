package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.databinding.FragmentAlbumsBinding
import com.babaetskv.muspert.presentation.albums.AlbumsPresenter
import com.babaetskv.muspert.presentation.albums.AlbumsView
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import com.babaetskv.muspert.ui.item.AlbumItem
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport

@ExperimentalPagedSupport
class AlbumsFragment : PlaybackFragment(), AlbumsView {
    @InjectPresenter
    lateinit var presenter: AlbumsPresenter

    private lateinit var adapter: FastAdapter<AlbumItem>
    private lateinit var itemAdapter: ItemAdapter<AlbumItem>
    private lateinit var binding: FragmentAlbumsBinding

    override val layoutResId: Int
        get() = R.layout.fragment_albums
    override val playbackControls: PlaybackControls
        get() = binding.viewPlaybackControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAlbumsBinding.bind(view)
        initRecyclerView()
        initListeners()
    }

    override fun showProgress() {
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    override fun showEmptyView(show: Boolean) {
        with (binding.emptyViewAlbums) {
            if (show) {
                setBanner(R.drawable.ic_empty_list)
                setTitle(R.string.empty_album_list_title)
                setMessage(R.string.empty_album_list_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.refreshAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun showErrorView(show: Boolean) {
        with (binding.emptyViewAlbums) {
            if (show) {
                setBanner(R.drawable.ic_error)
                setTitle(R.string.empty_album_list_title)
                setMessage(R.string.error_albums_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.refreshAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun populateAlbums(albums: List<Album>) {
        itemAdapter.setNewList(albums.map { AlbumItem(it) })
    }

    private fun initListeners() {
        binding.viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_refresh -> {
                    presenter.refreshAlbums()
                    true
                }
                else -> false
            }
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
                        binding.recyclerAlbums.post {
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
        binding.recyclerAlbums.apply {
            val orientation: Int
            layoutManager = LinearLayoutManager(requireContext()).also {
                orientation = it.orientation
            }
            adapter = this@AlbumsFragment.adapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context, orientation).apply {
                val color = ContextCompat.getColor(requireContext(), R.color.colorHint)
                setBackgroundColor(color)
            })
        }
    }
}
