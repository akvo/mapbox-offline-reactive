package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import android.util.Log
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Completable

class RenameOfflineRegion(context: Context, private val nameMapper: RegionNameMapper) {

    private val regionById = RegionById(context)

    fun execute(regionId: Long, newName: String): Completable {
        return regionById.findRegion(regionId).flatMapCompletable { renameArea(it, newName) }
    }

    private fun renameArea(region: OfflineRegion, newName: String): Completable {
        val metadata = nameMapper.getRegionMetadata(newName)

        return Completable.create { emitter ->
            region.updateMetadata(
                metadata,
                object : OfflineRegion.OfflineRegionUpdateMetadataCallback {
                    override fun onUpdate(metadata: ByteArray?) {
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    }

                    override fun onError(error: String?) {
                        Log.e(TAG, "Error: $error")
                        if (!emitter.isDisposed) {
                            emitter.onError(Exception(error))
                        }
                    }
                })
        }
    }

    companion object {
        private const val TAG = "RenameOfflineRegion"
    }
}
