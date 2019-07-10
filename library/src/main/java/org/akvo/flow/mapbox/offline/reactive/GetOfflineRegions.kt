package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import io.reactivex.Observable
import io.reactivex.Single

class GetOfflineRegions(context: Context) {
    private val regionsLister = OfflineRegionsLister(context)

    fun execute(): Single<List<Pair<OfflineRegion, OfflineRegionStatus>>> {
        return regionsLister.getRegionsList().flatMap { getRegionAndStatus(it) }
    }

    private fun getRegionAndStatus(regions: Array<OfflineRegion>): Single<List<Pair<OfflineRegion, OfflineRegionStatus>>> {
        return Observable.fromArray(* regions).concatMap { getOfflineRegionStatus(it).toObservable() }.toList()
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