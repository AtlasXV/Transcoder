package com.otaliastudios.transcoder.internal.video

import android.graphics.SurfaceTexture

/**
 * weiping@atlasv.com
 * 3/25/21
 */
interface GlDrawStrategy {
    fun onCreate(): SurfaceTexture
    fun onDestroy()
    fun getTextureTransform(): FloatArray
    fun drawFrame(scaleX: Float, scaleY: Float, rotation: Int)
}