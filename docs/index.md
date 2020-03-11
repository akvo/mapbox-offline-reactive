
## Mapbox Offline Reactive

### Why this library
In [our Flow mobile app](https://github.com/akvo/akvo-flow-mobile), we use [Mapbox](https://github.com/mapbox/mapbox-gl-native-android) to display maps. Since our app is used in remote areas we needed to provide an offline map functionality. We allow users to select small areas to download for later offline use. Here is a list of fonctionalities for the offline maps:
  * View a list of downloaded offline areas with their name
  * Rename an offline area
  * Delete an area
  * Select an area to use (the map will be centered on that area)
  
The [Mapbox SDK[(https://docs.mapbox.com/android/maps/overview/offline/) provides methods and classes to handle offline areas. Here is for example how we would download an area:

```
// Create the region asynchronously
offlineManager.createOfflineRegion(definition, metadata,
object : OfflineManager.CreateOfflineRegionCallback {
	override fun onCreate(offlineRegion: OfflineRegion) {
	    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
 
	    // Monitor the download progress using setObserver
	    offlineRegion.setObserver(object : OfflineRegion.OfflineRegionObserver {
			override fun onStatusChanged(status: OfflineRegionStatus) {
 
				// Calculate the download percentage
				val percentage = if (status.requiredResourceCount >= 0)
				    100.0 * status.completedResourceCount /status.requiredResourceCount else 0.0
 
				if (status.isComplete) {
				    // Download complete
				    Log.d(TAG, "Region downloaded successfully.")
				} else if (status.isRequiredResourceCountPrecise) {
				    Log.d(TAG, percentage)
				}
			}
 
		    override fun onError(error: OfflineRegionError) {
		        // If an error occurs, print to logcat
		        Log.e(TAG, "onError reason: " + error.reason)
		        Log.e(TAG, "onError message: " + error.message)
		    }
 
		    override fun mapboxTileCountLimitExceeded(limit: Long) {
		        // Notify if offline region exceeds maximum tile count
		        Log.e(TAG, "Mapbox tile count limit exceeded: $limit")
		    }
			})
	}
 
	override fun onError(error: String) {
	    Log.e(TAG, "Error: $error")
	}
})
```
In our app we try to use [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) with MVP pattern. We use RXJava and UseCases for async operations. As you can see, the callbacks above could not be easily added. Also here we have 2 chained callbacks for creating an area. We also want to unsubscribe if the user leaves that screen. Another reason, is an operation like rename or select, we have the area id but in mapbox you can list all areas, not just one so we needed a way to abstract this.

### Examples
The sample project that comes with the library shows most operations available with the library.

#### Create area

```
  //create some dummy area bounds
  val bounds = LatLngBounds.Builder()
                .include(computeOffset(latLng, 500.0, 45.0)) // Northeast
                .include(computeOffset(latLng, 500.0, 225.0)) // Southwest
                .build()
            val url = "mapbox://styles/mapbox/light-v10"
            val pixelRatio = resources.displayMetrics.density
            val zoom = 14.0
            val regionName = "${latLng.latitude}, ${latLng.longitude}"
            val createOfflineArea = CreateOfflineRegion(this.applicationContext, RegionNameMapper())

            val subscribeWith = createOfflineArea.execute(url, bounds, pixelRatio, zoom, regionName)
                .subscribeWith(object : DisposableCompletableObserver() {

                    override fun onComplete() {
                        Log.d(TAG, "Region created: $regionName")
                       	//update UI
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message, e)
                    }
                })

            disposables.add(subscribeWith)
```
#### Rename area

```
 val subscribeWith = RenameOfflineRegion(this, RegionNameMapper()).execute(id, randomName())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.d(TAG, "Region renamed")
                    //update UI
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.message, e)
                }
            })
        disposables.add(subscribeWith)
```

#### Delete Area

```
 val subscribeWith = DeleteOfflineRegion(this).execute(id)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.d(TAG, "Region deleted")
                    //Update UI
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, e.message, e)
                }
            })
        disposables.add(subscribeWith)
```

#### List all areas

```
 val subscribeWith = GetOfflineRegions(this).execute()
            .subscribeWith(object : DisposableSingleObserver<List<Pair<OfflineRegion, OfflineRegionStatus>>>() {
                override fun onSuccess(regions: List<Pair<OfflineRegion, OfflineRegionStatus>>) {
                   // use regions
                }

                override fun onError(e: Throwable) {
                    // display error
                }
            })
        disposables.add(subscribeWith)
```

### Further
The Mapbox team has also another library, the [offline plugin](https://docs.mapbox.com/android/plugins/overview/offline/) which allows to download maps using a background service but for our case it was not convenient as we wanted more control over how the maps were downloaded.
Since the begginning of writing this library, [Kotlin couroutines](https://developer.android.com/kotlin/coroutines) have been released and may be a better option instead of RXJava
