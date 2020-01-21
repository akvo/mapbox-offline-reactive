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

    fun getRegionMetadata(regionName: String): ByteArray {
        val jsonObject = JSONObject()
        jsonObject.put(JSON_FIELD_REGION_NAME, regionName)
        val json = jsonObject.toString()
        return json.toByteArray(charset(JSON_CHARSET))
    }

    companion object {
        private const val JSON_CHARSET = "UTF-8"
        private const val JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME"
        private const val TAG = "RegionNameMapper"
    }
}
