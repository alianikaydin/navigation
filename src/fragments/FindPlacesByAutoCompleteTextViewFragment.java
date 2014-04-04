package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import bingTrafficParser.*;
import com.example.navigation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import autocompletetext.AutoCompletePlaceDownloadTask;
import autocompletetext.AutoCompletePlaceParserTask;
import autocompletetext.AutoCompletePlaceUrl;

public class FindPlacesByAutoCompleteTextViewFragment extends
		SherlockMapFragment {
	public static GoogleMap googleMap;

	String bingUrl = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
	String bingKey = "AoHoD_fdpQD73-OoTNnnsGzYu5ClXmVNAGr2t-M_wKbR8TWHqKrZR1X6GHI5pzWm";
	String url2;

	private List<Marker> markers = new ArrayList<Marker>();

	public static AutoCompleteTextView textViewPlaces;

	AutoCompletePlaceDownloadTask placesDownloadTask;
	AutoCompletePlaceDownloadTask placeDetailsDownloadTask;
	AutoCompletePlaceParserTask placesParserTask;
	AutoCompletePlaceParserTask placeDetailsParserTask;

	public static final int PLACES = 0;
	public static final int PLACES_DETAILS = 1;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
		@Override
		public void run() {
			setHasOptionsMenu(true);
		}
	};

	public static FindPlacesByAutoCompleteTextViewFragment newInstance(
			int position, String title) {
		FindPlacesByAutoCompleteTextViewFragment fragment = new FindPlacesByAutoCompleteTextViewFragment();
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
		inflater.inflate(R.menu.auto_complete_text_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_bar_clear_locations) {
			clearMarkers();
		} else if (item.getItemId() == R.id.action_bar_toggle_style) {
			toggleStyle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		handler.postDelayed(runner, random.nextInt(2000));

		googleMap = getMap();

		View view = inflater.inflate(R.layout.auto_complete_text, container,
				false);
		textViewPlaces = (AutoCompleteTextView) view
				.findViewById(R.id.actv_places);
		textViewPlaces.setThreshold(1);
		
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

		textViewPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"
				placesDownloadTask = new AutoCompletePlaceDownloadTask(PLACES);

				// Getting url to the Google Places Autocomplete api
				String url = AutoCompletePlaceUrl.getAutoCompleteUrl(s
						.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		textViewPlaces.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

				HashMap<String, String> hm = (HashMap<String, String>) adapter
						.getItem(arg2);

				// Creating a DownloadTask to download Places details of
				// the
				// selected place
				placeDetailsDownloadTask = new AutoCompletePlaceDownloadTask(
						PLACES_DETAILS);

				// Getting url to the Google Places details api
				String url = AutoCompletePlaceUrl.getPlaceDetailsUrl(hm
						.get("reference"));

				// Start downloading Google Place Details
				// This causes to execute doInBackground() of
				// DownloadTask class
				placeDetailsDownloadTask.execute(url);

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
