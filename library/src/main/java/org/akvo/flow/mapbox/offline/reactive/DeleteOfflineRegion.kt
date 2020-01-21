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
import android.util.Log
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Completable

class DeleteOfflineRegion(context: Context) {

    private val regionById = RegionById(context)

    fun execute(regionId: Long): Completable {
        return regionById.findRegion(regionId).flatMapCompletable { deleteArea(it) }
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
        private const val TAG = "DeleteOfflineRegion"
    }
}
