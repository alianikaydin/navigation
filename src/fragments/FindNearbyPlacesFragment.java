package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import places.Place;
import places.PlaceDialogFragment;
import places.PlacesDownloadTask;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import bingTrafficParser.ParseTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.example.navigation.TabActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FindNearbyPlacesFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;
	
	String bingUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	String bingKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	String url2;

	private List<Marker> markers = new ArrayList<Marker>();

	private static final float UNDEFINED_COLOR = -1;

	Place[] nearPlaces;

	String[] nearPlacesType;

	String[] nearPlacesName;

	LatLng latLng;

	HashMap<String, Place> nearPlacesReference = new HashMap<String, Place>();

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static FindNearbyPlacesFragment newInstance(int position,
			String title) {
		FindNearbyPlacesFragment fragment = new FindNearbyPlacesFragment();
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
		inflater.inflate(R.menu.nearby_places_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		} else if (item.getItemId() == R.id.airport) {
			getNearbyPlaces("airport");
		} else if (item.getItemId() == R.id.restaurant) {
			getNearbyPlaces("restaurant");
		} else if (item.getItemId() == R.id.mosque) {
			getNearbyPlaces("mosque");
		} else if (item.getItemId() == R.id.movie_theater) {
			getNearbyPlaces("movie_theater");
		} else if (item.getItemId() == R.id.hospital) {
			getNearbyPlaces("hospital");
		} else if (item.getItemId() == R.id.bank) {
			getNearbyPlaces("bank");
		} else if (item.getItemId() == R.id.atm) {
			getNearbyPlaces("atm");
		} else if (item.getItemId() == R.id.cinema) {
			getNearbyPlaces("cinema");
		} else if (item.getItemId() == R.id.bus_station) {
			getNearbyPlaces("bus_station");
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		googleMap = getMap();

			
		nearPlacesReference = new HashMap<String, Place>();
		
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

		// Map Click listener
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				latLng = point;

				// Drawing a marker at the touched location
				addMarker(latLng, BitmapDescriptorFactory.HUE_GREEN);
			}
		});
		
		

		// Marker click listener
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				// If touched at User input location
				if (!nearPlacesReference.containsKey(marker.getId()))
					return false;

				Place place = nearPlacesReference.get(marker.getId());

				DisplayMetrics dm = new DisplayMetrics();

				WindowManager windowManager = (WindowManager) TabActivity.mainContext
						.getSystemService(TabActivity.WINDOW_SERVICE);

				Display display = windowManager.getDefaultDisplay();

				display.getMetrics(dm);

				// Creating a dialog fragment to display the photo
				PlaceDialogFragment dialogFragment = new PlaceDialogFragment(
						place, dm);

				FragmentManager fm = getFragmentManager();

				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add(dialogFragment, "TAG");

				fragmentTransaction.commit();

				return false;
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);

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

	private Marker addMarker(LatLng latLng, float color) {
		// Creating a marker
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting the position for the marker
		markerOptions.position(latLng);

		if (color != UNDEFINED_COLOR)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));

		// Placing a marker on the touched position
		Marker m = googleMap.addMarker(markerOptions);

		return m;
	}

	private void getNearbyPlaces(String type) {

		googleMap.clear();

		if (latLng == null) {
			Toast.makeText(getSherlockActivity(), "No points on the map!",
					Toast.LENGTH_LONG).show();
		} else {
			// PlacesParserTask.drawMarker(latLng,
			// BitmapDescriptorFactory.HUE_GREEN);

			StringBuilder sb = new StringBuilder(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			sb.append("location=" + latLng.latitude + "," + latLng.longitude);
			sb.append("&radius=500000");
			sb.append("&types=" + type);
			sb.append("&sensor=true");
			sb.append("&key=AIzaSyC-CiTPvezMf-xewmsVJZp8P8PcnWkSJow");

			// Creating a new non-ui thread task to download Google place //
			// jsondata
			PlacesDownloadTask placesDownloadTask = new PlacesDownloadTask();

			// Invokes the "doInBackground()" method of the class PlaceTask
			placesDownloadTask.execute(sb.toString());

		}
	}

}
