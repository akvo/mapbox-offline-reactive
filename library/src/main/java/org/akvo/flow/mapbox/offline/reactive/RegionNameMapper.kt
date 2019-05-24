package org.akvo.flow.mapbox.offline.reactive

import android.util.Log

import com.mapbox.mapboxsdk.offline.OfflineRegion
import org.json.JSONObject

class RegionNameMapper {

    fun getRegionName(offlineRegion: OfflineRegion): String {
        return try {
            val metadata = offlineRegion.metadata
            val json = String(metadata, charset(JSON_CHARSET))
            val jsonObject = JSONObject(json)
            jsonObject.getString(JSON_FIELD_REGION_NAME)
        } catch (exception: Throwable) {
            Log.e(TAG, "Failed to decode metadata: $exception.message")
            offlineRegion.id.toString() + ""
        }
    }

    fun getRegionMetadata(regionName: String): ByteArray? {
        return try {
            val jsonObject = JSONObject()
            jsonObject.put(JSON_FIELD_REGION_NAME, regionName)
            val json = jsonObject.toString()
            json.toByteArray(charset(JSON_CHARSET))
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to encode metadata: $exception.message")
            null
        }
    }

    companion object {
        private const val JSON_CHARSET = "UTF-8"
        private const val JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME"
        private const val TAG = "RegionNameMapper"
    }
}