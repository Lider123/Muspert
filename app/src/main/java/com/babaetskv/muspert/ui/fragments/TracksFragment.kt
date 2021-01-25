package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.tracks.TracksPresenter
import com.babaetskv.muspert.presentation.tracks.TracksView
import com.babaetskv.muspert.ui.EmptyDividerDecoration
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.item.TrackItem
import com.babaetskv.muspert.utils.notifier.Notifier
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_tracks.*
import kotlinx.android.synthetic.main.fragment_tracks.layoutPlaybackControls
import kotlinx.android.synthetic.main.fragment_tracks.toolbar
import kotlinx.android.synthetic.main.layout_playback_controls.*
import org.koin.android.ext.android.inject

class TracksFragment : BaseFragment(), TracksView {
    @InjectPresenter
    lateinit var presenter: TracksPresenter
    private val notifier: Notifier by inject()
    private val schedulersProvider: SchedulersProvider by inject()

    private val args: TracksFragmentArgs by navArgs()
    private lateinit var adapter: FastAdapter<TrackItem>
    private lateinit var itemAdapter: ItemAdapter<TrackItem>
    private var playbackDisposable: Disposable? = null

    override val layoutResId: Int
        get() = R.layout.fragment_tracks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onStart(args.album)
        initAdapter()
    }

    override fun onStart() {
        super.onStart()
        subscribeOnPlaybackService()
    }

    override fun onStop() {
        unsubscribeFromPlaybackService()
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
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

    private fun subscribeOnPlaybackService() {
        playbackDisposable = PlaybackService.updateViewSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextPlaybackCommand)
    }

    private fun unsubscribeFromPlaybackService() {
        playbackDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        if (data.track == null) {
            layoutPlaybackControls.setGone()
        } else {
            tvTrackTitle.text = data.track.title
            btnPlay.setImageResource(if (data.isPlaying) R.drawable.ic_pause_onprimary else R.drawable.ic_play_onprimary)
            btnPlay.setOnClickListener {
                PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_PLAY)
            }
            btnPrev.setOnClickListener {
                PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_PREV)
            }
            btnNext.setOnClickListener {
                PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_NEXT)
            }
            imgCover.setImageResource(R.drawable.logo_white)
            // TODO: do smth with cover and progress
            layoutPlaybackControls.setVisible()
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

    private fun initToolbar() {
        with (toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener {
                onBackPressed()
            }
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
                    // TODO
                    notifier.sendMessage("Clicked track ${item.track.title}") // TODO: remove
                    return false
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
}
