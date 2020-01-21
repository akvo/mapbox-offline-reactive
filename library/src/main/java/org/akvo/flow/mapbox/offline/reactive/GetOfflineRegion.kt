package org.akvo.flow.mapbox.offline.reactive

import android.content.Context
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.reactivex.Maybe

class GetOfflineRegion(context: Context) {

    private val regionById = RegionById(context)

    fun execute(regionId: Long): Maybe<OfflineRegion> {
        return regionById.findRegion(regionId)
    }
}
