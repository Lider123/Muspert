package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.presentation.albums.AlbumsPresenter
import com.babaetskv.muspert.presentation.albums.AlbumsView
import com.babaetskv.muspert.viewmodel.albums.AlbumsViewModel
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.item.AlbumItem
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import kotlinx.android.synthetic.main.fragment_albums.*
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalPagedSupport
class AlbumsFragment : BaseFragment(), AlbumsView {
    @InjectPresenter
    lateinit var presenter: AlbumsPresenter
    private val albumsViewModel: AlbumsViewModel by viewModel()

    private lateinit var adapter: FastAdapter<AlbumItem>
    private lateinit var itemAdapter: PagedModelAdapter<Album, AlbumItem>

    override val layoutResId: Int
        get() = R.layout.fragment_albums
    override val playbackControls: PlaybackControls?
        get() = viewPlaybackControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        initViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
        initListeners()
    }

    override fun showProgress() {
        swipe_container.isRefreshing = true
    }

    override fun hideProgress() {
        swipe_container.isRefreshing = false
    }

    override fun showEmptyView(show: Boolean) {
        with (emptyViewAlbums) {
            if (show) {
                setBanner(R.drawable.ic_empty_list)
                setTitle(R.string.empty_album_list_title)
                setMessage(R.string.empty_album_list_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(R.string.try_again)
                setActionCallback {
                    albumsViewModel.updateAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun showErrorView(show: Boolean) {
        with (emptyViewAlbums) {
            if (show) {
                setBanner(R.drawable.ic_error)
                setTitle(R.string.empty_album_list_title)
                setMessage(R.string.error_albums_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionText(R.string.try_again)
                setActionCallback {
                    albumsViewModel.updateAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun initListeners() {
        viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
    }

    private fun initAdapter() {
        val config = AsyncDifferConfig.Builder<Album>(Album.CALLBACK)
            .build()
        val placeholderInterceptor = { _: Int -> AlbumItem(null) }
        val placeholder = { album: Album ->  AlbumItem(album) }
        itemAdapter = PagedModelAdapter<Album, AlbumItem>(config, placeholderInterceptor, placeholder)
        adapter = FastAdapter.with(itemAdapter).apply {
            setHasStableIds(true)
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

    private fun initViewModel() {
        albumsViewModel.albumsLiveData.observe(this, Observer { albums ->
            itemAdapter.submitList(albums)
        })
        albumsViewModel.getState().observe(this, Observer { status ->
            presenter.onRequestStateChanged(status)
        })
    }

    private fun initToolbar() {
        with (toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initRecyclerView() {
        recyclerAlbums.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@AlbumsFragment.adapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
        swipe_container.setOnRefreshListener {
            albumsViewModel.updateAlbums()
        }
    }
}
