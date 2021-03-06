package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.databinding.FragmentFavoritesBinding
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.presentation.favorites.FavoritesPresenter
import com.babaetskv.muspert.presentation.favorites.FavoritesView
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import com.babaetskv.muspert.ui.item.TrackItem
import com.babaetskv.muspert.utils.*
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class FavoritesFragment : PlaybackFragment(), FavoritesView {
    private val binding: FragmentFavoritesBinding by viewBinding()
    private lateinit var adapter: FastAdapter<TrackItem>
    private lateinit var itemAdapter: ItemAdapter<TrackItem>
    private val presenter: FavoritesPresenter by moxyPresenter {
        FavoritesPresenter(get(), get(), get(), get(), get(), get(), get())
    }

    override val layoutResId: Int = R.layout.fragment_favorites
    override val playbackControls: PlaybackControls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onNextPlaybackCommand(data: PlaybackData) {
        super.onNextPlaybackCommand(data)
        val track = data.track
        val isPlaying = data.isPlaying
        itemAdapter.adapterItems.mapIndexed { position, item ->
            if (item.track.id == track?.id) {
                item.isPlaying = isPlaying
                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun showProgress() {
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    override fun showErrorView(show: Boolean) {
        with (binding.emptyViewTracks) {
            if (show) {
                setBanner(R.drawable.ic_error)
                setTitle(R.string.empty_track_list_title)
                setMessage(R.string.error_favorites_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.refreshFavorites()
                }
                setVisible()
            } else setGone()
        }
    }

    override fun populateTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            showEmptyView(true)
        } else {
            showEmptyView(false)
            itemAdapter.setNewList(tracks.map { TrackItem(it, it.id == PlaybackService.currTrackId) })
        }
    }

    override fun showEmptyView(show: Boolean) {
        with (binding.emptyViewTracks) {
            if (show) {
                setBanner(R.drawable.ic_empty_list)
                setTitle(R.string.empty_track_list_title)
                setMessage(R.string.empty_favorites_list_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(null)
                setActionCallback {
                    presenter.refreshFavorites()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        adapter = FastAdapter.Companion.with(itemAdapter).apply {
            setHasStableIds(true)
            val preloadMargin: Int = resources.getInteger(R.integer.preload_margin)
            onBindViewHolderListener = object : OnBindViewHolderListenerImpl<TrackItem>() {

                override fun onBindViewHolder(
                    viewHolder: RecyclerView.ViewHolder,
                    position: Int,
                    payloads: List<Any>
                ) {
                    super.onBindViewHolder(viewHolder, position, payloads)
                    if (position == adapter.itemCount - preloadMargin) {
                        binding.recyclerTracks.post {
                            presenter.loadNextPage()
                        }
                    }
                }
            }
            onClickListener = object : ClickListener<TrackItem> {

                override fun invoke(
                    v: View?,
                    adapter: IAdapter<TrackItem>,
                    item: TrackItem,
                    position: Int
                ): Boolean {
                    presenter.onSelectTrack(item.track)
                    return true
                }
            }
            addEventHook(object : ClickEventHook<TrackItem>() {

                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
                    if (viewHolder is TrackItem.ViewHolder) viewHolder.btnPlay else null

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<TrackItem>,
                    item: TrackItem
                ) {
                    if (item.track.id == PlaybackService.currTrackId) {
                        PlaybackService.sendAction(requireContext(), PlaybackService.Action.Play)
                    } else {
                        PlaybackService.startPlaybackService(requireContext(), PlaybackService.FAVORITES_ALBUM_ID, item.track.id)
                    }
                }
            })
            addEventHook(object : ClickEventHook<TrackItem>() {

                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
                    if (viewHolder is TrackItem.ViewHolder) viewHolder.btnOptions else null

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<TrackItem>,
                    item: TrackItem
                ) {
                    val data = item.track
                    v.showPopup(listOf(
                        PopupOption(
                            title = if (data.isFavorite) getString(R.string.remove_from_favorites) else getString(R.string.add_to_favorites),
                            action = { if (data.isFavorite) presenter.removeFromFavorites(data) else presenter.addToFavorites(data) }
                        )
                    ))
                }
            })
        }
    }

    companion object {

        fun newInstance() = FavoritesFragment().apply {
            arguments = bundleOf()
        }
    }
}
