package edu.uark.ahnelson.roomwithaview2024.MapsActivity

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import edu.uark.ahnelson.roomwithaview2024.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay




class OpenStreetMapFragment : Fragment(), Marker.OnMarkerClickListener {

    private lateinit var mMap: MapView
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    private lateinit var mCompassOverlay: CompassOverlay
    private var curLocation = GeoPoint(34.74, -92.28)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_open_street_map, container, false)
        mMap = root.findViewById(R.id.map)

        setupMapOptions()
        val mapController = mMap.controller
        mapController.setZoom(3.1)
        changeCenterLocation(curLocation)
        return root
    }

    override fun onResume() {
        super.onResume()
        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    private fun setupMapOptions() {
        mMap.isTilesScaledToDpi = true
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        addCopyrightOverlay()
        addLocationOverlay()
        addCameraButtonOverlay() // custom button for camera
        //addCompassOverlay()
        addMapScaleOverlay()
        addRotationOverlay()
    }

    private fun addCameraButtonOverlay() {
        val buttonOverlay = object : Overlay() {
            override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
                if (shadow) return

                val button = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_menu_camera, null)
                val buttonSize = 100 // Size of the button in pixels
                val margin = 20 // Margin from the bottom and right edges

                val x = mapView.width - buttonSize - margin
                val y = mapView.height - buttonSize - margin

                button?.setTint(Color.BLACK)
                button?.setBounds(x, y, x + buttonSize, y + buttonSize)
                button?.draw(canvas)
            }

            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                val buttonSize = 100 // Size of the button in pixels
                val margin = 20 // Margin from the bottom and right edges

                val x = mapView.width - buttonSize - margin
                val y = mapView.height - buttonSize - margin

                if (e.x >= x && e.x <= x + buttonSize && e.y >= y && e.y <= y + buttonSize) {
                    // Handle button click
                    Log.d("OpenStreetMapFragment", "Camera button clicked")
                    return true
                }
                return false
            }
        }

        mMap.overlays.add(buttonOverlay)
    }

    private fun addRotationOverlay() {
        val rotationGestureOverlay = RotationGestureOverlay(mMap)
        rotationGestureOverlay.isEnabled
        mMap.setMultiTouchControls(true)
        mMap.overlays.add(rotationGestureOverlay)
    }

    private fun addLocationOverlay() {
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mMap)
        this.mLocationOverlay.enableMyLocation();
        mMap.overlays.add(mLocationOverlay)
    }

    private fun addCompassOverlay() {
        mCompassOverlay = CompassOverlay(context, InternalCompassOrientationProvider(context), mMap)
        mCompassOverlay.enableCompass()
        mMap.overlays.add(mCompassOverlay)
    }

    private fun addCopyrightOverlay() {
        val copyrightNotice: String =
            mMap.tileProvider.tileSource.copyrightNotice
        val copyrightOverlay = CopyrightOverlay(context)
        copyrightOverlay.setCopyrightNotice(copyrightNotice)
        mMap.getOverlays().add(copyrightOverlay)

    }

    private fun addMapScaleOverlay() {
        val dm: DisplayMetrics = context?.resources?.displayMetrics ?: return
        val scaleBarOverlay = ScaleBarOverlay(mMap)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        mMap.overlays.add(scaleBarOverlay)
    }

    fun changeCenterLocation(geoPoint: GeoPoint) {
        curLocation = geoPoint
        val mapController = mMap.controller
        mapController.setCenter(curLocation);


    }

    fun addMarker(geoPoint: GeoPoint, id: Int) {
        val startMarker = Marker(mMap)
        startMarker.position = geoPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.setOnMarkerClickListener(this)
        startMarker.id = id.toString()


        startMarker.icon = ResourcesCompat.getDrawable(resources, R.drawable.map_pin_small, null)
        mMap.getOverlays().add(startMarker)

    }

    fun clearMarkers() {
        mMap.overlays.clear()
        setupMapOptions()
    }

    fun clearOneMarker(id: Int) {
        for (overlay in mMap.overlays) {
            if (overlay is Marker) {
                if (overlay.id == id.toString()) {
                    mMap.overlays.remove(overlay)
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
        marker?.id?.let {
            Log.d("OpenStreetMapFragment", it)
            // TODO: launch detail view of marker, with id as parameter
            // the detailed activity will fetch all photos and descriptions with that location
        }
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment OpenStreetMapFragment.
         */
        @JvmStatic
        fun newInstance() =
            OpenStreetMapFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}