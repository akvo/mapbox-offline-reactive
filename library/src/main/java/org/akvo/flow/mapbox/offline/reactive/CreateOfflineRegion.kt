package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import android.util.Log
import com.mapbox.mapboxsdk.constants.MapboxConstants
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition
import io.reactivex.Completable
import kotlin.math.max
import kotlin.math.min

class CreateOfflineRegion(private val context: Context, private val nameMapper: RegionNameMapper) {

    fun execute(
        styleUrl: String,
        bounds: LatLngBounds,
        pixelRatio: Float,
        zoom: Double,
        regionName: String
    ): Completable {
        val minZoom = max(zoom - ZOOM_MAX, MapboxConstants.MINIMUM_ZOOM.toDouble())
        val maxZoom = min(zoom + ZOOM_MAX, MapboxConstants.MAXIMUM_ZOOM.toDouble())
        val definition = OfflineTilePyramidRegionDefinition(styleUrl, bounds, minZoom, maxZoom, pixelRatio)
        val metadata = nameMapper.getRegionMetadata(regionName)

        return Completable.create { emitter ->
            OfflineManager.getInstance(context).createOfflineRegion(definition, metadata,
                object : OfflineManager.CreateOfflineRegionCallback {

                    override fun onCreate(offlineRegion: OfflineRegion) {
                        Log.d(TAG, "Offline region created: $regionName")
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    }

                    override fun onError(error: String) {
                        Log.e(TAG, "Error: $error")
                        if (!emitter.isDisposed) {
                            emitter.onError(Exception(error))
                        }
                    }
                })
        }
    }

    companion object {
        private const val TAG = "CreateOfflineRegion"
        private const val ZOOM_MAX = 2
    }
}