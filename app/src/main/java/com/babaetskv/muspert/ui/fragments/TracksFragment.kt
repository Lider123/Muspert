package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.tracks.TracksPresenter
import com.babaetskv.muspert.presentation.tracks.TracksView
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.item.TrackItem
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tracks.*

class TracksFragment : BaseFragment(), TracksView {
    @InjectPresenter
    lateinit var presenter: TracksPresenter

    private val args: TracksFragmentArgs by navArgs()
    private lateinit var adapter: FastAdapter<TrackItem>
    private lateinit var itemAdapter: ItemAdapter<TrackItem>

    override val layoutResId: Int
        get() = R.layout.fragment_tracks
    override val playbackControls: PlaybackControls?
        get() = viewPlaybackControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListeners()
    }

    override fun populateAlbum(album: Album) {
        view?.post {
            toolbar.title = album.title
            Picasso.with(requireContext())
                .load(BuildConfig.API_URL + album.cover)
                .placeholder(R.drawable.logo_white)
                .error(R.drawable.logo_white)
                .resize(0, 600)
                .into(imgBackdrop)
        }
    }

    override fun showProgress() {
        progress.setVisible()
    }

    override fun hideProgress() {
        progress.setGone()
    }

    override fun showErrorView(show: Boolean) {
        with (emptyViewTracks) {
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
            itemAdapter.setNewList(tracks.map { TrackItem(it) })
        }
    }

    private fun showEmptyView(show: Boolean) {
        with (emptyViewTracks) {
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
        recyclerTracks.apply {
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
                    (viewHolder as? TrackItem.ViewHolder)?.buttonPlayPause

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<TrackItem>,
                    item: TrackItem
                ) {
                    // TODO
                    PlaybackService.startPlaybackService(requireContext(), item.track.albumId, item.track.id)
                }
            })
        }
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
    }

    @ProvidePresenter
    fun providePresenter() = TracksPresenter(args.album)
}
