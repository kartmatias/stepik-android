package org.stepik.android.view.glide.model

import android.graphics.Bitmap
import com.caverock.androidsvg.SVG

sealed class ImageResource {
    data class Vector(val svg: SVG) : ImageResource()
    data class Raster(val bitmap: Bitmap) : ImageResource()
}