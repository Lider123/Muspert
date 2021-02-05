package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.presentation.catalog.CatalogPresenter
import com.babaetskv.muspert.presentation.catalog.CatalogView
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.item.AlbumSmallItem
import com.babaetskv.muspert.ui.item.GenreSmallItem
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.fragment_catalog.*

class CatalogFragment : BaseFragment(), CatalogView {
    @InjectPresenter
    lateinit var presenter: CatalogPresenter

    private lateinit var albumsAdapter: FastAdapter<IItem<*>>
    private lateinit var albumsItemAdapter: ItemAdapter<IItem<*>>
    private lateinit var genresAdapter: FastAdapter<IItem<*>>
    private lateinit var genresItemAdapter: ItemAdapter<IItem<*>>

    override val layoutResId: Int
        get() = R.layout.fragment_catalog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViews()
        initListeners()
    }

    override fun showProgress() {
        progress.setVisible()
    }

    override fun hideProgress() {
        progress.setGone()
    }

    override fun populateAlbums(albums: List<Album>) {
        if (albums.isEmpty()) {
            recyclerAlbums.setGone()
            showAlbumsEmptyView(true)
        } else {
            recyclerAlbums.setVisible()
            showAlbumsEmptyView(false)
            albumsItemAdapter.setNewList(albums.map { AlbumSmallItem(it) })
        }
    }

    override fun populateGenres(genres: List<Genre>) {
        if (genres.isEmpty()) {
            recyclerGenres.setGone()
            showGenresEmptyView(true)
        } else {
            recyclerGenres.setVisible()
            showGenresEmptyView(false)
            genresItemAdapter.setNewList(genres.map { GenreSmallItem(it) })
        }
    }

    override fun showAlbumsErrorView(show: Boolean) {
        with (emptyViewAlbums) {
            if (show) {
                setIcon(R.drawable.ic_error)
                setMessage(R.string.error_albums_message)
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionIcon(R.drawable.ic_refresh_accent)
                setActionCallback {
                    presenter.loadAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun showGenresErrorView(show: Boolean) {
        with (emptyViewGenres) {
            if (show) {
                setIcon(R.drawable.ic_error)
                setMessage(R.string.error_genres_message)
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionIcon(R.drawable.ic_refresh_accent)
                setActionCallback {
                    presenter.loadGenres()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun showAlbumsEmptyView(show: Boolean) {
        with (emptyViewAlbums) {
            if (show) {
                setIcon(R.drawable.ic_empty_list)
                setMessage(R.string.empty_album_list_message)
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionIcon(R.drawable.ic_refresh_accent)
                setActionCallback {
                    presenter.loadAlbums()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun showGenresEmptyView(show: Boolean) {
        with (emptyViewGenres) {
            if (show) {
                setIcon(R.drawable.ic_empty_list)
                setMessage(R.string.empty_genre_list_message)
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionIcon(R.drawable.ic_refresh_accent)
                setActionCallback {
                    presenter.loadGenres()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun initAdapters() {
        albumsItemAdapter = ItemAdapter()
        genresItemAdapter = ItemAdapter()
        albumsAdapter = FastAdapter.Companion.with(albumsItemAdapter).apply {
            setHasStableIds(true)
            onClickListener = object : ClickListener<IItem<*>> {

                override fun invoke(
                    v: View?,
                    adapter: IAdapter<IItem<*>>,
                    item: IItem<*>,
                    position: Int
                ): Boolean = if (item is AlbumSmallItem) {
                    presenter.onSelectAlbum(item.album)
                    true
                } else false
            }
        }
        genresAdapter = FastAdapter.Companion.with(genresItemAdapter).apply {
            setHasStableIds(true)
            onClickListener = object : ClickListener<IItem<*>> {

                override fun invoke(
                    v: View?,
                    adapter: IAdapter<IItem<*>>,
                    item: IItem<*>,
                    position: Int
                ): Boolean = if (item is GenreSmallItem) {
                    presenter.onSelectGenre(item.genre)
                    true
                } else false
            }
        }
    }

    private fun initRecyclerViews() {
        recyclerAlbums.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = albumsAdapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
        recyclerGenres.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = genresAdapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
    }

    private fun initListeners() {
        tvAlbums.setOnClickListener {
            presenter.onAlbumsClick()
        }
        tvGenres.setOnClickListener {
            presenter.onGenresClick()
        }
    }
}
