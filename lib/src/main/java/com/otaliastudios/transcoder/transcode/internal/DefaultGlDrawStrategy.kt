package com.otaliastudios.transcoder.transcode.internal

import android.graphics.SurfaceTexture
import android.opengl.Matrix
import com.otaliastudios.opengl.draw.GlRect
import com.otaliastudios.opengl.program.GlTextureProgram
import com.otaliastudios.opengl.texture.GlTexture

/**
 * weiping@atlasv.com
 * 2/24/21
 */
class DefaultGlDrawStrategy : GlDrawStrategy {
    private lateinit var mProgram: GlTextureProgram
    private lateinit var mDrawable: GlRect
    override fun onCreate(): SurfaceTexture {
        val texture = GlTexture()
        mProgram = GlTextureProgram()
        mProgram.texture = (texture)
        mDrawable = GlRect()
        return SurfaceTexture(texture.id)
    }

    override fun onDestroy() {
        mProgram.release()
    }

    override fun getTextureTransform(): FloatArray {
        return mProgram.textureTransform
    }

    override fun drawFrame(scaleX: Float, scaleY: Float, rotation: Int) {
        // Invert the scale.
        val glScaleX: Float = 1f / scaleX
        val glScaleY: Float = 1f / scaleY
        // Compensate before scaling.
        val glTranslX = (1f - glScaleX) / 2f
        val glTranslY = (1f - glScaleY) / 2f
        Matrix.translateM(mProgram.textureTransform, 0, glTranslX, glTranslY, 0f)
        // Scale.
        Matrix.scaleM(mProgram.textureTransform, 0, glScaleX, glScaleY, 1f)
        // Apply rotation.
        Matrix.translateM(mProgram.textureTransform, 0, 0.5f, 0.5f, 0f)
        Matrix.rotateM(mProgram.textureTransform, 0, rotation.toFloat(), 0f, 0f, 1f)
        Matrix.translateM(mProgram.textureTransform, 0, -0.5f, -0.5f, 0f)
        // Draw.
        mProgram.draw(mDrawable)
    }
}