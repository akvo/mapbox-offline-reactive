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
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Single

class OfflineRegionsLister(private val context: Context) {

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
