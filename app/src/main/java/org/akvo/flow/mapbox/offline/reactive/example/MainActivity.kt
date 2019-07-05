package org.akvo.flow.mapbox.offline.reactive.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.akvo.flow.mapbox.offline.reactive.CreateOfflineArea
import org.akvo.flow.mapbox.offline.reactive.GetOfflineAreasList
import org.akvo.flow.mapbox.offline.reactive.RegionNameMapper
import org.akvo.flow.mapbox.offline.reactive.RenameOfflineArea
import java.lang.Math.asin
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity(), AreaListener {

    private val disposables = CompositeDisposable()
    private val adapter = AreasAdapter(nameMapper = RegionNameMapper(), areaListener = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWt2byIsImEiOiJzUFVwR3pJIn0.8dLa4fHG19fBwwBUJMDOSQ")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        areasList.layoutManager = LinearLayoutManager(this)
        areasList.adapter = adapter
        loadAreas()

        fab.setOnClickListener {
            fab.hide()
            createInitialAreas()
        }
    }

    private fun loadAreas() {
        val subscribeWith = GetOfflineAreasList(this).execute()
            .subscribeWith(object : DisposableSingleObserver<List<Pair<OfflineRegion, OfflineRegionStatus>>>() {
                override fun onSuccess(regions: List<Pair<OfflineRegion, OfflineRegionStatus>>) {
                    adapter.setRegions(regions)
                    if (regions.isEmpty()) {
                        fab.show()
                    } else {
                        fab.hide()
                    }
                }

                override fun onError(e: Throwable) {
                    Snackbar.make(areasList, "Error loading areas", Snackbar.LENGTH_LONG).show()
                    fab.show()
                }
            })
        disposables.add(subscribeWith)
    }

    private fun createInitialAreas() {
        val latLngList = listOf(
            LatLng(37.113340, 70.852440),
            LatLng(37.185330, 70.790380),
            LatLng(37.197740, 70.762520),
            LatLng(37.024220, 70.439600),
            LatLng(37.027300, 70.432070)
        )
        for (latLng in latLngList) {
            val bounds = LatLngBounds.Builder()
                .include(computeOffset(latLng, 500.0, 45.0)) // Northeast
                .include(computeOffset(latLng, 500.0, 225.0)) // Southwest
                .build()
            val url = "mapbox://styles/mapbox/light-v10"
            val pixelRatio = resources.displayMetrics.density
            val zoom = 14.0
            val regionName = "${latLng.latitude}, ${latLng.longitude}"
            val createOfflineArea = CreateOfflineArea(this.applicationContext, RegionNameMapper())

            val subscribeWith = createOfflineArea.execute(url, bounds, pixelRatio, zoom, regionName)
                .subscribeWith(object : DisposableCompletableObserver() {

                    override fun onComplete() {
                        Log.d(TAG, "Region created: $regionName")
                        loadAreas()
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message, e)
                    }
                })

            disposables.add(subscribeWith)
        }
    }

    private fun randomName(): String {
        return "region-${nextInt(0, 1000)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    override fun rename(id: Long) {
        val subscribeWith = RenameOfflineArea(this, RegionNameMapper()).execute(id, randomName())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.d(TAG, "Region renamed")
                    loadAreas()
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.message, e)
                }
            })
        disposables.add(subscribeWith)
    }

    private fun computeOffset(from: LatLng, distance: Double, heading: Double): LatLng {
        var distance = distance
        var heading = heading
        distance /= EARTH_RADIUS
        heading = toRadians(heading)
        // http://williams.best.vwh.net/avform.htm#LL
        val fromLat = toRadians(from.latitude)
        val fromLng = toRadians(from.longitude)
        val cosDistance = cos(distance)
        val sinDistance = sin(distance)
        val sinFromLat = sin(fromLat)
        val cosFromLat = cos(fromLat)
        val sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading)
        val dLng = atan2(sinDistance * cosFromLat * sin(heading), cosDistance - sinFromLat * sinLat)
        return LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng))
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val EARTH_RADIUS = 6371009
    }
}
