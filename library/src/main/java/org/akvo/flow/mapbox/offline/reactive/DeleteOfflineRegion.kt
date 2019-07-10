package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import android.util.Log
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Completable

class DeleteOfflineRegion(context: Context) {

    private val regionById = RegionById(context)

    fun execute(regionId: Long): Completable {
        return regionById.getOfflineRegion(regionId).flatMapCompletable { deleteArea(it) }
    }

    private fun deleteArea(region: OfflineRegion): Completable {
        return Completable.create { emitter ->
            region.delete(object : OfflineRegion.OfflineRegionDeleteCallback {
                override fun onDelete() {
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
        private const val TAG = "RenameOfflineArea"
    }
}
