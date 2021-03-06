package com.babaetskv.muspert.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentHomeBinding
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.app.presentation.home.HomePresenter
import com.babaetskv.muspert.app.presentation.home.HomeView
import com.babaetskv.muspert.app.ui.EmptyDividerDecoration
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.ui.item.AlbumSmallItem
import com.babaetskv.muspert.app.ui.item.GenreSmallItem
import com.babaetskv.muspert.app.utils.setGone
import com.babaetskv.muspert.app.utils.setVisible
import com.babaetskv.muspert.app.utils.viewBinding
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class HomeFragment : BaseFragment(), HomeView {
    private val presenter: HomePresenter by moxyPresenter {
        HomePresenter(get(), get(), get(), get(), get(), get())
    }
    private lateinit var albumsAdapter: FastAdapter<IItem<*>>
    private lateinit var albumsItemAdapter: ItemAdapter<IItem<*>>
    private lateinit var genresAdapter: FastAdapter<IItem<*>>
    private lateinit var genresItemAdapter: ItemAdapter<IItem<*>>
    private val binding: FragmentHomeBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_home

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
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    override fun populateAlbums(albums: List<Album>) {
        if (albums.isEmpty()) {
            binding.recyclerAlbums.setGone()
            showAlbumsEmptyView(true)
        } else {
            binding.recyclerAlbums.setVisible()
            showAlbumsEmptyView(false)
            albumsItemAdapter.setNewList(albums.map { AlbumSmallItem(it) })
        }
    }

    override fun populateGenres(genres: List<Genre>) {
        if (genres.isEmpty()) {
            binding.recyclerGenres.setGone()
            showGenresEmptyView(true)
        } else {
            binding.recyclerGenres.setVisible()
            showGenresEmptyView(false)
            genresItemAdapter.setNewList(genres.map { GenreSmallItem(it) })
        }
    }

    override fun showAlbumsErrorView(show: Boolean) {
        with (binding.emptyViewAlbums) {
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
        with (binding.emptyViewGenres) {
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
        with (binding.emptyViewAlbums) {
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
        with (binding.emptyViewGenres) {
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
        binding.recyclerAlbums.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = albumsAdapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
        binding.recyclerGenres.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = genresAdapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
    }

    private fun initListeners() {
        binding.tvAlbums.setOnClickListener {
            presenter.onAlbumsClick()
        }
        binding.tvGenres.setOnClickListener {
            presenter.onGenresClick()
        }
    }
}
