package bingTrafficParser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParseTask extends AsyncTask<String, Void, JSONObject> {

	GoogleMap gm;
	public static ArrayList<LatLng> positionList = new ArrayList<LatLng>();
	

	public ParseTask(GoogleMap googleMap) {
		gm = googleMap;

	}


	public ParseTask() {
		// TODO Auto-generated constructor stub
	}


	protected JSONObject doInBackground(String... params) {
		JSONParser jParser = new JSONParser();
		final JSONObject jobject = jParser.getJSONFromUrl(params[0]);
		return jobject;

	}

	
	@Override
	protected void onPostExecute(JSONObject jobject) {
		String str_estimatedTotal = null;
		String str_type = null;
		String strdescription = null;
		String strlane = null;
		String str_zero = null;
		String strroadClosed = null;
		String strroadseverity = null;
		
		

		try {

			JSONArray jarray = jobject.getJSONArray("resourceSets");
			System.out.println("dateNow jarray :" + jarray.length());
			for (int i = 0; i < jarray.length(); i++) {
				if (!jarray.isNull(i)) {
					JSONObject jobjresources = jarray.getJSONObject(i);
					System.out.println("dateNow jobjresources :"
							+ jobjresources.length());
					// estimatedTotal
					if (!jobjresources.isNull("estimatedTotal")) {
						str_estimatedTotal = jobjresources
								.getString("estimatedTotal");
						System.out.println("resources str_estimatedTotal :"
								+ str_estimatedTotal);

					} else {
						System.out
								.println("resources str_estimatedTotal NULL for :"
										+ i + " ITEM");
					}
					if (!jobjresources.isNull("resources")) {
						// resources
						JSONArray jarrresources = jobjresources
								.getJSONArray("resources");

						for (int j = 0; j < jarrresources.length(); j++) {
							System.out.println("$$$$$$$$$$ ITEM " + j
									+ " START $$$$$$$$$$$$$$$$#");
							if (!jarrresources.isNull(j)) {

								JSONObject jobjjarrresources = jarrresources
										.getJSONObject(j);
								if (!jobjjarrresources.isNull("__type")) {
									// __type"

									str_type = jobjjarrresources
											.getString("__type");
									System.out.println("resources str_type :"
											+ str_type);
								} else {
									System.out
											.println("resources __type NULL for :"
													+ j + " ITEM");
								}
								// description"
								if (!jobjjarrresources.isNull("description")) {
									strdescription = jobjjarrresources
											.getString("description");
									System.out
											.println("resources description :"
													+ strdescription);
								} else {
									System.out
											.println("resources description NULL for :"
													+ j + " ITEM");
								}
								// lane"
								if (!jobjjarrresources.isNull("lane")) {
									strlane = jobjjarrresources
											.getString("lane");
									System.out.println("resources lane :"
											+ strlane);
								} else {
									System.out
											.println("resources lane NULL for :"
													+ j + " ITEM");
								}
								// lane"
								if (!jobjjarrresources.isNull("point")) {
									JSONObject jobjpoint = jobjjarrresources
											.getJSONObject("point");
									
									// point
									if (!jobjpoint.isNull("coordinates")) {
										JSONArray jarcoordinates = jobjpoint
												.getJSONArray("coordinates");
										for (int k = 0; k < jarcoordinates
												.length(); k++) {
											// JSONObject
											// jobjcoordinates=jarcoordinates.getString(k);
											if (!jarcoordinates.isNull(k)) {
												str_zero = jarcoordinates
														.getString(k);
												System.out
														.println("coordinates :"
																+ k
																+ ": "
																+ str_zero);
												// adding to map as a marker
												double lat = (Double) jarcoordinates
														.get(0);
												double lon = (Double) jarcoordinates
														.get(1);
												LatLng positionLatLng = new LatLng(
														lat, lon);
												
														
													positionList.add(positionLatLng);
												gm.addMarker(new MarkerOptions()
														.position(
																positionLatLng)
														.title(strdescription));
												
												
											}

											else {
												System.out
														.println("coordinates :"
																+ k
																+ " is NULL:"
																+ j + " ITEM");
											}
										}
									} else {
										System.out
												.println("resources coordinates NULL for :"
														+ j + " ITEM");
									}
								} else {
									System.out
											.println("resources point NULL for :"
													+ j + " ITEM");
								}

								// roadClosed"
								// lane"
								if (!jobjjarrresources.isNull("roadClosed")) {

									strroadClosed = jobjjarrresources
											.getString("roadClosed");
									System.out.println("resources roadClosed :"
											+ strroadClosed);
								} else {
									System.out
											.println("resources roadClosed NULL for :"
													+ j + " ITEM");
								}
								// severity"
								if (!jobjjarrresources.isNull("severity")) {
									strroadseverity = jobjjarrresources
											.getString("severity");
									System.out.println("resources severity :"
											+ strroadseverity);
								} else {
									System.out
											.println("resources severity NULL for :"
													+ j + " ITEM");
								}
							} else {
								System.out
										.println("jarrresources    NULL for :"
												+ j + " ITEM");
							}

							System.out.println("##################### ITEM "
									+ j + " END ##############");
						}
					} else {
						System.out.println("resources    NULL for :" + i
								+ " ITEM");
					}
				}

				else {
					System.out.println("resources     NULL for : ITEM");
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
}