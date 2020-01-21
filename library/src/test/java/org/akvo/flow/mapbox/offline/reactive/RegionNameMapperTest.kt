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

import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.offline.OfflineRegion
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class RegionNameMapperTest {

    private val mockOfflineRegion: OfflineRegion = Mockito.mock(OfflineRegion::class.java)

    @Test
    fun getRegionNameShouldReturnIdIfException() {
        `when`(mockOfflineRegion.metadata).thenReturn(JsonObject().toString().toByteArray(charset("UTF-8")))
        `when`(mockOfflineRegion.id).thenReturn(123)

        val mapper = RegionNameMapper()

        val mapped = mapper.getRegionName(mockOfflineRegion)

        Assert.assertEquals("123", mapped)
    }

    @Test
    fun getRegionNameShouldReturnRegionNameCorrectly() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("FIELD_REGION_NAME", "region1")
        `when`(mockOfflineRegion.metadata).thenReturn(jsonObject.toString().toByteArray(charset("UTF-8")))
        `when`(mockOfflineRegion.id).thenReturn(123)

        val mapper = RegionNameMapper()

        val mapped = mapper.getRegionName(mockOfflineRegion)

        Assert.assertEquals("region1", mapped)
    }
}
