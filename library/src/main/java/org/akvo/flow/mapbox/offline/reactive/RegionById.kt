package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Single

class RegionById(context: Context) {

    private val regionsLister = OfflineRegionsLister(context)

    fun getOfflineRegion(regionId: Long): Single<OfflineRegion> {
        return regionsLister.getRegionsList()
            .map { offlineRegions -> offlineRegions.first { regionId == it.id } }
    }
}
