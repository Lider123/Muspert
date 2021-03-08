package com.babaetskv.muspert.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.babaetskv.muspert.R

/**
 * @author Konstantin on 26.06.2020
 */
class AppNavigator {
    private val navBuilder: NavOptions.Builder
        get() = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_right)
            .setPopExitAnim(R.anim.slide_out_right)
    var controller: NavController? = null

    fun forward(action: NavDirections) {
        navBuilder
            .build()
            .let {
                controller?.navigate(action, it)
            }
    }

    fun replaceWith(vararg actions: NavDirections) {
        actions.forEachIndexed { index, action ->
            if (index == 0) replaceWith(action) else forward(action)
        }
    }

    fun replaceWith(action: NavDirections) =
        navBuilder
            .setPopUpTo(controller?.currentDestination!!.id, true)
            .build()
            .let {
                controller?.navigate(action, it)
            }

    fun newStack(action: NavDirections) =
        navBuilder
            .setPopUpTo(R.id.nav_graph, true)
            .build()
            .let {
                controller?.navigate(action, it)
            }

    fun back() = controller?.navigateUp()
}
