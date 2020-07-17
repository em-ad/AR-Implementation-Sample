package com.shliama.augmentedvideotutorial

import android.animation.ValueAnimator
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.*
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.animation.doOnStart
import androidx.core.graphics.rotationMatrix
import androidx.core.graphics.transform
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.shliama.augmentedvideotutorial.DataHandling.Dataholder
import java.io.IOException

open class ArVideoFragment : ArFragment(), MediaPlayer.OnPreparedListener {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var externalTexture: ExternalTexture
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var videoAnchorNode: VideoAnchorNode

    private var activeAugmentedImage: AugmentedImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        arSceneView.isLightEstimationEnabled = false
        initializeSession()
        createArScene()
    }

    override fun getSessionConfiguration(session: Session): Config {

        fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {

            try {
                config.augmentedImageDatabase = AugmentedImageDatabase(session).also { db ->
                    for(item in Dataholder.photosBitmaps.indices){
                        db.addImage("test_video_1.mp4", Dataholder.photosBitmaps[item])
                    }
                }
                return true
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Could not add bitmap to augmented image database" + e.message)
            } catch (e: IOException) {
                Log.e(TAG, "IO exception loading augmented image bitmap.", e)
            }
            return false
        }

        return super.getSessionConfiguration(session).also {
            it.lightEstimationMode = Config.LightEstimationMode.DISABLED
            it.focusMode = Config.FocusMode.AUTO

            if (!setupAugmentedImageDatabase(it, session)) {
                Toast.makeText(requireContext(), "در ساخت دیتابیس واقعیت افزوده مشکلی پیش آمد", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createArScene() {
        externalTexture = ExternalTexture().also {
            mediaPlayer.setSurface(it.surface)
        }

        ModelRenderable.builder()
            .setSource(requireContext(), R.raw.augmented_video_model)
            .build()
            .thenAccept { renderable ->
                videoRenderable = renderable
                renderable.isShadowCaster = false
                renderable.isShadowReceiver = false
                renderable.material.setExternalTexture("videoTexture", externalTexture)
            }
            .exceptionally { throwable ->
                Log.e(TAG, "مدل قابل پخش ساخته نشد", throwable)
                return@exceptionally null
            }

        videoAnchorNode = VideoAnchorNode().apply {
            setParent(arSceneView.scene)
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        val frame = arSceneView.arFrame ?: return

        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)

        // If current active augmented image isn't tracked anymore and video playback is started - pause video playback
        val nonFullTrackingImages =
            updatedAugmentedImages.filter { it.trackingMethod != AugmentedImage.TrackingMethod.FULL_TRACKING }
        activeAugmentedImage?.let { activeAugmentedImage ->
            if (isArVideoPlaying() && nonFullTrackingImages.any { it.index == activeAugmentedImage.index }) {
                pauseArVideo()
            }
        }

        val fullTrackingImages =
            updatedAugmentedImages.filter { it.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING }
        if (fullTrackingImages.isEmpty()) {
            Log.e(TAG, "%%%EMPTY%%%")
            return
        }

        // If current active augmented image is tracked but video playback is paused - resume video playback
        activeAugmentedImage?.let { activeAugmentedImage ->
            if (fullTrackingImages.any { it.index == activeAugmentedImage.index }) {
                if (!isArVideoPlaying()) {
                    resumeArVideo()
                }
                return
            }
        }

        // Otherwise - make the first tracked image active and start video playback
        fullTrackingImages.firstOrNull()?.let { augmentedImage ->
            try {
                playbackArVideo(augmentedImage)
            } catch (e: Exception) {
                Log.e(TAG, " در پخش ویدیو خطایی رخ داد${augmentedImage.name}]", e)
            }
        }
    }

    private fun isArVideoPlaying() = mediaPlayer.isPlaying

    private fun pauseArVideo() {
        videoAnchorNode.renderable = null
        mediaPlayer.pause()
    }

    private fun resumeArVideo() {
        mediaPlayer.start()
        fadeInVideo()
    }

    private fun dismissArVideo() {
        videoAnchorNode.anchor?.detach()
        videoAnchorNode.renderable = null
        activeAugmentedImage = null
        mediaPlayer.reset()
    }

    private fun playbackArVideo(augmentedImage: AugmentedImage) {
//        val toast: Toast = Toast.makeText(context, "در حال بارگزاری محتوای ویدیو...", Toast.LENGTH_SHORT)
//        val toastLayout: LinearLayout = toast.view as LinearLayout
//        val toastTV: TextView = toastLayout.getChildAt(0) as TextView
//        toastTV.typeface = ResourcesCompat.getFont(context!!, R.font.app_font)
//        toast.show()

        requireContext().assets.openFd(augmentedImage.name)
            .use { descriptor ->

                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(
                    descriptor.fileDescriptor,
                    descriptor.startOffset,
                    descriptor.length
                )

                val videoWidth =
                    metadataRetriever.extractMetadata(METADATA_KEY_VIDEO_WIDTH).toFloatOrNull()
                        ?: 0f
                val videoHeight =
                    metadataRetriever.extractMetadata(METADATA_KEY_VIDEO_HEIGHT).toFloatOrNull()
                        ?: 0f
                val videoRotation =
                    metadataRetriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION).toFloatOrNull()
                        ?: 0f

                val imageSize = RectF(0f, 0f, augmentedImage.extentX, augmentedImage.extentZ)
                    .transform(rotationMatrix(videoRotation))

                val videoScaleType = VideoScaleType.CenterCrop

                videoAnchorNode.setVideoProperties(
                    videoWidth = videoWidth,
                    videoHeight = videoHeight,
                    videoRotation = videoRotation,
                    imageWidth = imageSize.width(),
                    imageHeight = imageSize.height(),
                    videoScaleType = videoScaleType
                )

                // Update the material parameters
                videoRenderable.material.setFloat2(
                    MATERIAL_IMAGE_SIZE,
                    imageSize.width(),
                    imageSize.height()
                )
                videoRenderable.material.setFloat2(MATERIAL_VIDEO_SIZE, videoWidth, videoHeight)
                videoRenderable.material.setBoolean(MATERIAL_VIDEO_CROP, VIDEO_CROP_ENABLED)

                mediaPlayer.reset()
                Log.e(TAG, "playbackArVideo: "  + Dataholder.photos[augmentedImage.index].second )
                mediaPlayer.setDataSource(Dataholder.photos[augmentedImage.index].second as String)
            }.also {
                mediaPlayer.isLooping = true
                mediaPlayer.prepare()
                mediaPlayer.start()
            }


        videoAnchorNode.anchor?.detach()
        videoAnchorNode.anchor = augmentedImage.createAnchor(augmentedImage.centerPose)

        activeAugmentedImage = augmentedImage

        externalTexture.surfaceTexture.setOnFrameAvailableListener {
            it.setOnFrameAvailableListener(null)
            fadeInVideo()
        }
    }

    private fun fadeInVideo() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200L
            interpolator = LinearInterpolator()
            addUpdateListener { v ->
                videoRenderable.material.setFloat(MATERIAL_VIDEO_ALPHA, v.animatedValue as Float)
            }
            doOnStart { videoAnchorNode.renderable = videoRenderable }
            start()
        }
    }

    override fun onPause() {
        super.onPause()
        dismissArVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val TAG = "ArVideoFragment"

        private const val VIDEO_CROP_ENABLED = true

        private const val MATERIAL_IMAGE_SIZE = "imageSize"
        private const val MATERIAL_VIDEO_SIZE = "videoSize"
        private const val MATERIAL_VIDEO_CROP = "videoCropEnabled"
        private const val MATERIAL_VIDEO_ALPHA = "videoAlpha"
    }

    override fun onPrepared(p0: MediaPlayer?) {
        Log.e(TAG, "Media Player Prepared Successfully" )
    }
}