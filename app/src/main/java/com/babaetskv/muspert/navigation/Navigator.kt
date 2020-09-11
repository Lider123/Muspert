package com.babaetskv.muspert.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.babaetskv.muspert.R

/**
 * @author Konstantin on 26.06.2020
 */
class AppNavigator {
    var controller: NavController? = null

    fun forward(@IdRes dest: Int, args: Bundle? = null) = controller?.navigate(dest, args)

    fun replaceWith(@IdRes dest: Int, args: Bundle? = null) =
        NavOptions.Builder()
            .setPopUpTo(controller?.currentDestination!!.id, true)
            .build()
            .let {
                controller?.navigate(dest, args, it)
            }

    fun newStack(@IdRes dest: Int, args: Bundle? = null) =
        NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()
            .let {
                controller?.navigate(dest, args, it)
            }

    fun back() = controller?.navigateUp()
}
