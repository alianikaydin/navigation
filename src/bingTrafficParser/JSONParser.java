package bingTrafficParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream iStream = null;
	static JSONArray jarray = null;
	static JSONObject jObj= null;
	static String json = "";

	public JSONParser() {
	}

	
	///// HATA VAR/////**********///
	public JSONObject getJSONFromUrl(String url) {
		

		// Making HTTP request
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//HttpPost httpPost = new HttpPost(url);
		HttpGet get = new HttpGet(url);

	
		try {
			HttpResponse httpResponse = httpClient.execute(get);
			//String json = EntityUtils.toString(httpResponse.getEntity());
			//System.out.println(json);
			
			 
			HttpEntity httpEntity = httpResponse.getEntity();
			iStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(iStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			iStream.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parsing the string to a JSON object
		try {
			if (json != null) {
				jObj = new JSONObject(json);
			} else {
				jObj = null;
			}
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	
	
	}
}

