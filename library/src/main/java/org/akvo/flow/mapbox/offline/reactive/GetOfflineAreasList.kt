package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import io.reactivex.Observable
import io.reactivex.Single

class GetOfflineAreasList(private val context: Context) {

    fun execute(): Single<List<Pair<OfflineRegion, OfflineRegionStatus>>> {
        return getRegionsList().flatMap { getRegionAndStatus(it) }
    }

    private fun getRegionAndStatus(regions: Array<OfflineRegion>): Single<List<Pair<OfflineRegion, OfflineRegionStatus>>> {
        return Observable.fromArray(* regions).concatMap { getOfflineRegionStatus(it).toObservable() }.toList()
    }

    private fun getRegionsList(): Single<Array<OfflineRegion>> {
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

    private fun getOfflineRegionStatus(region: OfflineRegion): Single<Pair<OfflineRegion, OfflineRegionStatus>> {
        return Single.create { emitter ->
            region.getStatus(
                object : OfflineRegion.OfflineRegionStatusCallback {

                    override fun onStatus(status: OfflineRegionStatus) {
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(Pair(region, status))
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