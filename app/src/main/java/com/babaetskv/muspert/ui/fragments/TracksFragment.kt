package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.databinding.FragmentTracksBinding
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.presentation.tracks.TracksPresenter
import com.babaetskv.muspert.presentation.tracks.TracksView
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.item.TrackItem
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.babaetskv.muspert.utils.viewBinding
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.squareup.picasso.Picasso
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class TracksFragment : PlaybackFragment(), TracksView {
    private val args: TracksFragmentArgs by navArgs()
    private val presenter: TracksPresenter by moxyPresenter {
        TracksPresenter(args.album, get(), get(), get(), get(), get())
    }
    private lateinit var adapter: FastAdapter<TrackItem>
    private lateinit var itemAdapter: ItemAdapter<TrackItem>
    private val binding: FragmentTracksBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_tracks
    override val playbackControls: PlaybackControls
        get() = binding.viewPlaybackControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListeners()
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

    override fun populateAlbum(album: Album) {
        view?.post {
            binding.toolbar.title = album.title
            Picasso.with(requireContext())
                .load(BuildConfig.API_URL + album.cover)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .resize(0, 600)
                .into(binding.imgBackdrop)
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
                setMessage(R.string.error_tracks_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.loadTracks()
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
            itemAdapter.setNewList(tracks.map { TrackItem(it, it.id == PlaybackService.trackId) })
        }
    }

    private fun showEmptyView(show: Boolean) {
        with (binding.emptyViewTracks) {
            if (show) {
                setBanner(R.drawable.ic_empty_list)
                setTitle(R.string.empty_track_list_title)
                setMessage(R.string.empty_track_list_message)
                setTitleColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setMessageColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground))
                setActionText(R.string.try_again)
                setActionCallback {
                    presenter.loadTracks()
                }
                setVisible()
            } else setGone()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TracksFragment.adapter
            itemAnimator = null
            addItemDecoration(EmptyDividerDecoration(requireContext(), R.dimen.layout_baseline_default))
        }
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        adapter = FastAdapter.Companion.with(itemAdapter).apply {
            setHasStableIds(true)
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
                    if (item.track.id == PlaybackService.trackId) {
                        PlaybackService.sendAction(requireContext(), PlaybackService.Action.Play)
                    } else {
                        PlaybackService.startPlaybackService(requireContext(), item.track.albumId, item.track.id)
                    }
                }
            })
        }
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
    }
}
