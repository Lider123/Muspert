package com.babaetskv.muspert.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.arellomobile.mvp.MvpAppCompatFragment
import com.babaetskv.muspert.R

/**
 * @author Konstantin on 13.05.2020
 */
abstract class BaseFragment : MvpAppCompatFragment() {
    protected lateinit var navController: NavController
        private set

    @get:LayoutRes
    abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment)
    }
}
