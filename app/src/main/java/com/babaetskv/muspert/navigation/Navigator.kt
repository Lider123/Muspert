package com.babaetskv.muspert.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.babaetskv.muspert.R

/**
 * @author Konstantin on 26.06.2020
 */
class AppNavigator {
    var controller: NavController? = null

    fun forward(action: NavDirections) = controller?.navigate(action)

    fun replaceWith(action: NavDirections) =
        NavOptions.Builder()
            .setPopUpTo(controller?.currentDestination!!.id, true)
            .build()
            .let {
                controller?.navigate(action, it)
            }

    fun newStack(action: NavDirections) =
        NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()
            .let {
                controller?.navigate(action, it)
            }

    fun back() = controller?.navigateUp()
}
