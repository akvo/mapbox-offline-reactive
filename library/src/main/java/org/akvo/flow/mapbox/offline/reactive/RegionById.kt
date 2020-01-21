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
import io.reactivex.Maybe

class RegionById(context: Context) {

    private val regionsLister = OfflineRegionsLister(context)

    fun findRegion(regionId: Long): Maybe<OfflineRegion> {
        return regionsLister.getRegionsList().flatMapMaybe { findRegion(it, regionId) }
    }

    private fun findRegion(regions: Array<OfflineRegion>, regionId: Long): Maybe<OfflineRegion> {
        val offlineRegion = regions.firstOrNull { regionId == it.id }
        return offlineRegion?.let { Maybe.just(it) } ?: Maybe.empty()
    }
}
