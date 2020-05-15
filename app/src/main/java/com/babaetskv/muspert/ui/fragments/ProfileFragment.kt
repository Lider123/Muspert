package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.presentation.profile.ProfilePresenter
import com.babaetskv.muspert.presentation.profile.ProfileView
import com.babaetskv.muspert.ui.base.BaseFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.setParams()
    }

    override fun populateData(data: User) {
        usernameTextView.text = data.username
    }
}
