package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.navigation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import directions.DirectionsDownloadTask;
import directions.DirectionsMarkers;
import directions.DirectionsParserTask;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TravellingModeFragment extends SherlockMapFragment {

	public static GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();

	public static RadioButton rbDriving;
	public static RadioButton rbBiCycling;
	public static RadioButton rbWalking;
	RadioGroup rgModes;

	public static ArrayList<LatLng> markerPoints;

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
		} else if (item.getItemId() == R.id.walking) {

			if (markerPoints.size() >= 2) {
				LatLng origin = markerPoints.get(0);
				LatLng dest = markerPoints.get(1);

				// Getting URL to the Google Directions API
				String url = DirectionsParserTask
						.getDirectionsUrl(origin, dest);

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

		googleMap = getMap();

		View view = inflater
				.inflate(R.layout.travelling_mode, container, false);

		rgModes = (RadioGroup) view.findViewById(R.id.rg_modes);

		rbDriving = (RadioButton) view.findViewById(R.id.rb_driving);

		rbBiCycling = (RadioButton) view.findViewById(R.id.rb_bicycling);

		rbWalking = (RadioButton) view.findViewById(R.id.rb_walking);

		markerPoints = new ArrayList<LatLng>();

		rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2) {
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);

					// Getting URL to the Google Directions API
					String url = DirectionsParserTask.getDirectionsUrl(origin,
							dest);

					DirectionsDownloadTask directionsdownloadTask = new DirectionsDownloadTask();

					// Start downloading json data from Google Directions API
					directionsdownloadTask.execute(url);
				}

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

				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2) {
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					// Getting URL to the Google Directions API
					String url = DirectionsParserTask.getDirectionsUrl(origin,
							dest);

					DirectionsDownloadTask directionsDownloadTask = new DirectionsDownloadTask();

					// Start downloading json data from Google Directions API
					directionsDownloadTask.execute(url);
				}

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

}