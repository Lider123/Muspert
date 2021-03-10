package com.babaetskv.muspert.app.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.babaetskv.muspert.app.ui.base.PlaybackControls
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target

fun RequestCreator.into(controls: PlaybackControls) =
    into(object : Target {

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            controls.setCover(bitmap)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            controls.setCover(errorDrawable)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            controls.setCover(placeHolderDrawable)
        }
    })
