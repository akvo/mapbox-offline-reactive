package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import android.util.Log
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Completable

class DeleteOfflineArea(private val context: Context) {

    private val regionsLister = OfflineRegionsLister(context)

    fun execute(regionId: Long): Completable {
        return regionsLister.getRegionsList().flatMapCompletable { deleteArea(it, regionId) }
    }

    private fun deleteArea(regions: Array<OfflineRegion>, regionId: Long): Completable {
        val region = regions.first { regionId == it.id }

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
