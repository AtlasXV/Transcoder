package com.otaliastudios.transcoder.transcode.internal

import android.graphics.SurfaceTexture

/**
 * weiping@atlasv.com
 * 2/24/21
 */
interface GlDrawStrategy {
    fun onCreate(): SurfaceTexture
    fun onDestroy()
    fun getTextureTransform(): FloatArray
    fun drawFrame(scaleX: Float, scaleY: Float, rotation: Int)
}