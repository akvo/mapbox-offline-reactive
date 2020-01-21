/*
 * Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
