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
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private val adapter = AreasAdapter(nameMapper = RegionNameMapper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWt2byIsImEiOiJzUFVwR3pJIn0.8dLa4fHG19fBwwBUJMDOSQ")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        areasList.layoutManager = LinearLayoutManager(this)
        areasList.adapter = adapter

        loadAreas()

        fab.setOnClickListener {
            createArea()
        }
    }

    private fun loadAreas() {
        val subscribeWith = GetOfflineAreasList(this).execute()
            .subscribeWith(object : DisposableSingleObserver<List<Pair<OfflineRegion, OfflineRegionStatus>>>() {
                override fun onSuccess(regions: List<Pair<OfflineRegion, OfflineRegionStatus>>) {
                    adapter.setRegions(regions)
                }

                override fun onError(e: Throwable) {
                    Snackbar.make(areasList, "Error loading areas", Snackbar.LENGTH_LONG).show()
                }
            })
        disposables.add(subscribeWith)
    }

    private fun createArea() {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(37.7897, -119.5073)) // Northeast
            .include(LatLng(37.6744, -119.6815)) // Southwest
            .build()
        val url = "mapbox://styles/mapbox/light-v10"
        val pixelRatio = resources.displayMetrics.density
        val zoom = 14.0
        val regionName = regionName()
        val createOfflineArea = CreateOfflineArea(this.applicationContext, RegionNameMapper())

        val subscribeWith = createOfflineArea.execute(url, bounds, pixelRatio, zoom, regionName)
            .subscribeWith(object : DisposableCompletableObserver() {

                override fun onComplete() {
                    Log.d(TAG, "Region created: $regionName")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.message, e)
                }
            })

        disposables.add(subscribeWith)
    }

    private fun regionName(): String {
        return "region-${nextInt(0, 1000)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
