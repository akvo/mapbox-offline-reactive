package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Single

class GetOfflineRegion(context: Context) {

    private val regionById = RegionById(context)

    fun execute(regionId: Long): Single<OfflineRegion> {
        return regionById.getOfflineRegion(regionId)
    }
}
