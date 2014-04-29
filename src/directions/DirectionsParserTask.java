package directions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import bingTrafficParser.ParseTask;

import com.example.navigation.TabActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import fragments.TravellingModeFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DirectionsParserTask extends
		AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

	static String mode = "mode=driving";

	@Override
	protected List<List<HashMap<String, String>>> doInBackground(
			String... params) {
		JSONObject jObject;
		List<List<HashMap<String, String>>> routes = null;

		try {
			jObject = new JSONObject(params[0]);
			DirectionsJSONParser parser = new DirectionsJSONParser();

			// Starts parsing data
			routes = parser.parse(jObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return routes;
	}

	@Override
	protected void onPostExecute(List<List<HashMap<String, String>>> result) {

		ArrayList<LatLng> points = new ArrayList<LatLng>();
		PolylineOptions lineOptions = new PolylineOptions();
		HashMap<String, String> point = new HashMap<String, String>();
		try {

			if (result != null) {
				// Traversing through all the routes
				for (int i = 0; i < result.size(); i++) {

					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);

					// Fetching all the points in i-th route
					for (int j = 0; j < path.size(); j++) {
						point = path.get(j);

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						points.add(position);

					}

					// ///////////////////////////////checking whether there is
					// a incident or not
					outerloop: for (int j = 0; j < ParseTask.positionList
							.size(); j++) {
						for (int j2 = 0; j2 < points.size(); j2++) {
							LatLng pos = ParseTask.positionList.get(j);

							if (liesOnSegment(points.get(j2),
									points.get(j2 + 1), pos)) {
								Toast.makeText(TabActivity.mainContext,
										"THERE IS AN INCIDENT IN THE ROUTE",
										Toast.LENGTH_LONG).show();

								break outerloop;

							} else {
								break;
							}
						}

					}

					// /////////////////////////////////////////////////////////////////////////

					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(10);

					// Changing the color polyline according to the mode
					if (TravellingModeFragment.travelling_mode == 1)
						lineOptions.color(Color.RED);
					else if (TravellingModeFragment.travelling_mode == 2)
						lineOptions.color(Color.GREEN);
					else if (TravellingModeFragment.travelling_mode == 3)
						lineOptions.color(Color.BLUE);

					// Drawing polyline in the Google Map for the i-th route
					TravellingModeFragment.googleMap.addPolyline(lineOptions);

				}

			}

			else {
				Toast.makeText(TabActivity.mainContext,
						"No points on the map!", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {

			if (result.size() < 1) {
				Toast.makeText(TabActivity.mainContext, "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

	}

	// finds coordinates between two coordinates//////////////////
	boolean liesOnSegment(LatLng a, LatLng b, LatLng c) {

		double crossProduct = (c.longitude - b.longitude)
				* (b.latitude - a.latitude) - (c.latitude - b.latitude)
				* (b.longitude - a.longitude);
		double dotProduct = (c.latitude - a.latitude)
				* (c.latitude - b.latitude) + (c.longitude - a.longitude)
				* (c.longitude - b.longitude);
		double squaredlengthba = (b.latitude - a.latitude)
				* (b.latitude - a.latitude) + (b.longitude - a.longitude)
				* (b.longitude - a.longitude);

		/*double distance = Math.abs(((b.longitude - a.longitude) * c.latitude)
				+ ((a.latitude - b.latitude) * c.longitude) - b.latitude
				* b.longitude + a.longitude * b.latitude - a.latitude
				* b.longitude + b.latitude * b.longitude)
				/ Math.sqrt((b.longitude - a.longitude)
						* (b.longitude - a.longitude)
						+ (a.latitude - b.latitude) * (a.latitude - b.latitude));

		if(distance==0)
			return true;
		*/ 
		
		
		if (Math.abs(crossProduct) < Math.E && dotProduct > 0
				&& dotProduct < squaredlengthba)
			return true;
		else
			return false;
	}

	public static String getDirectionsUrl(LatLng origin, LatLng dest,
			int travelling_mode) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		if (travelling_mode == 1) {
			mode = "mode=driving";

		} else if (travelling_mode == 2) {
			mode = "mode=bicycling";

		} else if (travelling_mode == 3) {
			mode = "mode=walking";

		}

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

}
