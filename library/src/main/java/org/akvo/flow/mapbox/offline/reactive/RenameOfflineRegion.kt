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
