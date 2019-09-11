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
