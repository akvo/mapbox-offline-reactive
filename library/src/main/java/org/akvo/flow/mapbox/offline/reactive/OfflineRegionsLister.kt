package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Single

class OfflineRegionsLister (private val context: Context) {

    fun getRegionsList(): Single<Array<OfflineRegion>> {
        return Single.create { emitter ->
            OfflineManager.getInstance(context)
                .listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {

                    override fun onList(offlineRegions: Array<OfflineRegion>) {
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(offlineRegions)
                        }
                    }

                    override fun onError(error: String) {
                        if (!emitter.isDisposed) {
                            emitter.onError(Exception(error))
                        }
                    }
                })
        }
    }
}