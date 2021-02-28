package com.babaetskv.muspert.ui.fragments

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.databinding.FragmentProfileBinding
import com.babaetskv.muspert.presentation.profile.ProfilePresenter
import com.babaetskv.muspert.presentation.profile.ProfileView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.dialog.TwoChoiceDialogParams
import com.babaetskv.muspert.utils.dialog.showDialog
import com.babaetskv.muspert.utils.notifier.Notifier
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.babaetskv.muspert.utils.viewBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import io.reactivex.Observable
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

/**
 * @author Konstantin on 13.05.2020
 */
class ProfileFragment : BaseFragment(), ProfileView {
    private val notifier: Notifier by inject()
    private val schedulersProvider: SchedulersProvider by inject()

    private val presenter: ProfilePresenter by moxyPresenter {
        ProfilePresenter(get(), schedulersProvider, get(), get(), get(), notifier)
    }
    private val binding: FragmentProfileBinding by viewBinding()
    private val cropOptions: UCrop.Options
        get() = UCrop.Options().apply {
            val primaryColor = ContextCompat.getColor(requireContext(), R.color.colorBackground)
            val accentColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            val textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
            setToolbarColor(primaryColor)
            setStatusBarColor(accentColor)
            setActiveWidgetColor(accentColor)
            setHideBottomControls(false)
            setShowCropGrid(false)
            setShowCropFrame(false)
            setToolbarWidgetColor(textColor)
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(100)
            withMaxResultSize(1000, 1000)
            withAspectRatio(1f, 1f)
            setToolbarTitle(getString(R.string.edit_photo))
        }

    override val layoutResId: Int
        get() = R.layout.fragment_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun populateData(data: User) {
        binding.usernameTextView.text = getString(R.string.username_placeholder, data.firstName, data.lastName)
        binding.nicknameTextView.text = data.nickname
        data.avatar?.let {
            Picasso.with(requireContext())
                .load(BuildConfig.API_URL + it)
                .resize(0, 400)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(binding.avatarImageView)
        } ?: run {
            binding.avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder)
        }
    }

    override fun showProgress() {
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            TwoChoiceDialogParams(
                schema = TwoChoiceDialogParams.Schema.ACCENT_RIGHT,
                message = getString(R.string.logout_message),
                leftActionText = getString(R.string.yes),
                rightActionText = getString(R.string.no),
                onLeftActionClick = { dialog ->
                    dialog.dismiss()
                    presenter.onLogout()
                },
                onRightActionClick = { dialog ->
                    dialog.dismiss()
                }
            ).let {
                requireContext().showDialog(it)
            }
        }
        binding.avatarImageView.setOnClickListener {
            TwoChoiceDialogParams(
                schema = TwoChoiceDialogParams.Schema.ACCENT_BOTH,
                message = getString(R.string.edit_avatar),
                leftActionText = getString(R.string.camera),
                rightActionText = getString(R.string.gallery),
                onLeftActionClick = { dialog ->
                    Dexter.withContext(requireContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(object : PermissionListener {

                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                openCamera()
                                dialog.dismiss()
                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                notifier.sendMessage(R.string.camera_permission_not_granted)
                                dialog.dismiss()
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?
                            ) = Unit
                        })
                        .check()
                },
                onRightActionClick = { dialog ->
                    Dexter.withContext(requireContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {

                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                openGallery()
                                dialog.dismiss()
                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                notifier.sendMessage(R.string.read_storage_permission_not_granted)
                                dialog.dismiss()
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?
                            ) = Unit
                        })
                        .check()
                }
            ).let {
                requireContext().showDialog(it)
            }
        }
    }

    private fun openCamera() {
        RxPaparazzo.single(this)
            .crop(cropOptions)
            .usingCamera()
            .let {
                subscribeToGetImage(it)
            }
    }

    private fun openGallery() {
        RxPaparazzo.single(this)
            .crop(cropOptions)
            .usingGallery()
            .let {
                subscribeToGetImage(it)
            }
    }

    private fun subscribeToGetImage(observable: Observable<Response<ProfileFragment, FileData>>) {
        observable
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .doOnNext { response ->
                if (response.resultCode() != Activity.RESULT_OK) {
                    return@doOnNext
                }
                val uri = Uri.fromFile(response.data().file)
                presenter.onChangeAvatar(uri)
            }
            .onErrorResumeNext(Observable.empty())
            .subscribe()

    }
}
