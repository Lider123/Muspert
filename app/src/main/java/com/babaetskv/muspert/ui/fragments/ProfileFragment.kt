package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.presentation.profile.ProfilePresenter
import com.babaetskv.muspert.presentation.profile.ProfileView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.dialog.TwoChoiceDialogParams
import com.babaetskv.muspert.utils.dialog.showDialog
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * @author Konstantin on 13.05.2020
 */
class ProfileFragment : BaseFragment(),
    ProfileView {
    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override val layoutResId: Int
        get() = R.layout.fragment_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun populateData(data: User) {
        usernameTextView.text = getString(R.string.username_placeholder, data.firstName, data.lastName)
        nicknameTextView.text = data.nickname
        // TODO: set avatar
    }

    private fun initListeners() {
        settingsButton.setOnClickListener {
            navigator.forward(R.id.action_main_to_settings)
        }
        avatarImageView.setOnClickListener {
            TwoChoiceDialogParams(
                message = getString(R.string.edit_avatar),
                leftActionText = getString(R.string.camera),
                rightActionText = getString(R.string.gallery),
                onLeftActionClick = { dialog ->
                    // TODO: pick photo from camera
                    dialog.dismiss()
                },
                onRightActionClick = { dialog ->
                    // TODO: pick photo from gallery
                    dialog.dismiss()
                }
            ).let {
                requireContext().showDialog(it)
            }
        }
    }
}
