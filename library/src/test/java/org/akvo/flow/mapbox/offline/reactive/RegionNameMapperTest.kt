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