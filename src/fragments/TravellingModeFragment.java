package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bingTrafficParser.ParseTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import directions.DirectionsDownloadTask;
import directions.DirectionsMarkers;
import directions.DirectionsParserTask;

public class TravellingModeFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	String bingUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	String bingKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	String url2;

	private List<Marker> markers = new ArrayList<Marker>();

	public static ArrayList<LatLng> markerPoints;

	public static int travelling_mode;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static TravellingModeFragment newInstance(int position, String title) {
		TravellingModeFragment fragment = new TravellingModeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("title", title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.travelling_mode_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		} else if (item.getItemId() == R.id.driving) {

			travelling_mode = 1;

			if (markerPoints.size() >= 2) {
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);

				// Getting URL to the Google Directions API
				String url = DirectionsParserTask.getDirectionsUrl(origin,
						dest, travelling_mode);

				DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

				// Start downloading json data from Google Directions API
				directionsdownloadTask.execute(url);
			}
		} else if (item.getItemId() == R.id.bicycling) {

			travelling_mode = 2;

			if (markerPoints.size() >= 2) {
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);

				// Getting URL to the Google Directions API
				String url = DirectionsParserTask.getDirectionsUrl(origin,
						dest, travelling_mode);

				DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

				// Start downloading json data from Google Directions API
				directionsdownloadTask.execute(url);
			}
		} else if (item.getItemId() == R.id.walking) {

			travelling_mode = 3;

			if (markerPoints.size() >= 2) {
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);

				// Getting URL to the Google Directions API
				String url = DirectionsParserTask.getDirectionsUrl(origin,
						dest, travelling_mode);

				DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

				// Start downloading json data from Google Directions API
				directionsdownloadTask.execute(url);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		View view = inflater
				.inflate(R.layout.travelling_mode, container, false);
		FragmentManager fragmentManager = getFragmentManager();
		SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map);
		googleMap = supportMapFragment.getMap();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.commit();
		
		//googleMap = getMap();

		markerPoints = new ArrayList<LatLng>();

		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				LatLngBounds bounds = googleMap.getProjection()
						.getVisibleRegion().latLngBounds;
				if (getAreaInTheScreen(bounds) < 5000000) {
					new ParseTask(googleMap).execute(urlBuilder(bounds));
				} else {
					googleMap.clear();
				}

			}

			private String urlBuilder(LatLngBounds bounds) {
				double northLat = bounds.northeast.latitude;
				double northLong = bounds.northeast.longitude;
				double southLat = bounds.southwest.latitude;
				double southLong = bounds.southwest.longitude;

				url2 = bingUrl + String.valueOf(southLat) + ","
						+ String.valueOf(southLong) + ","
						+ String.valueOf(northLat) + ","
						+ String.valueOf(northLong) + "?key=" + bingKey;
				return url2;

			}

			private float getAreaInTheScreen(LatLngBounds bounds) {
				double northLat = bounds.northeast.latitude;
				double northLong = bounds.northeast.longitude;
				double southLat = bounds.southwest.latitude;
				double southLong = bounds.southwest.longitude;

				Location l = new Location("southwest");
				l.setLatitude(southLat);
				l.setLongitude(southLong);
				Location l2 = new Location("southeast");
				l.setLatitude(southLat);
				l.setLongitude(northLong);
				float length = l.distanceTo(l2);

				Location l3 = new Location("northwest");
				l3.setLatitude(northLat);
				l3.setLongitude(southLong);
				float width = l.distanceTo(l3);

				float area = (Math.abs(width) / 1000)
						* (Math.abs(length) / 1000);

				return area;
			}
		});

		// Setting a click event handler for the map
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {

				// Already two locations
				if (markerPoints.size() > 1) {
					markerPoints.clear();
					googleMap.clear();
				}

				// Adding new item to the ArrayList
				markerPoints.add(arg0);

				// Draws Start and Stop markers on the Google Map
				DirectionsMarkers.drawStartStopMarkers();

			}
		});

		return view;
	}

	public void toggleStyle() {
		if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	public void clearMarkers() {
		googleMap.clear();
		markers.clear();
	}

}
