package snae.tmcandroid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import snae.tmcandroid.traffic.TMURL;
import snae.tmcandroid.traffic.TMURLManager;

public class TMHttpUtil {
	private static final String TAG="TMHttpUtil";
	private static final String json = "application/json";
	private static final String form = "application/x-www-form-urlencoded";
	private static final String charset = "UTF-8";
	
	public static String getStringFromInputStream(InputStream in, String encoding) throws IOException{	
		BufferedReader reader = null;
		if (encoding==null){
			reader = new BufferedReader(new InputStreamReader(in));
		}else{
			reader = new BufferedReader(new InputStreamReader(in, encoding));
		}
        String result, line = reader.readLine();
        result = line;
        while((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
	}
	
	public static String getStringFromInputStream(InputStream in) throws IOException{		
		return getStringFromInputStream(in, null);
	}
	
	public static void getAndConsumeUrl(TMURLManager tmMgr, String url) throws IOException{
		TMURL tmurl1 = tmMgr.getUrl(url);
		HttpURLConnection con = tmurl1.getHttpUrlConnection();
		con.setRequestMethod("GET");
        int code = con.getResponseCode();
		if (code == HttpURLConnection.HTTP_OK){
			InputStream is = con.getInputStream();
	        TMHttpUtil.getStringFromInputStream(is);
			is.close();
		}
	}
	
	public static String getContentFromGetURL(String strUrl){
		try{
			URL url = new URL(strUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", form);
			con.setRequestProperty("Accept", json);
			if (con.getResponseCode()==HttpURLConnection.HTTP_OK){
				InputStream is = con.getInputStream();
				String rspBody = TMHttpUtil.getStringFromInputStream(is);
				is.close();
				return rspBody;
			}else{
				return null;
			}
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
	}
	
	public static String getConentFromPostURL(String strUrl, String query){
		try{
			HttpURLConnection con = (HttpURLConnection) new URL(strUrl).openConnection();
			con.setDoOutput(true); //post
			con.setRequestProperty("Content-Type", form);
			con.setRequestProperty("Accept", json);
			OutputStream os = con.getOutputStream();
			os.write(query.getBytes(charset));
			os.close();
			if (con.getResponseCode()==HttpURLConnection.HTTP_OK){
				InputStream is = con.getInputStream();
				String rspBody = TMHttpUtil.getStringFromInputStream(is);
				is.close();
				return rspBody;
			}else{
				Log.e(TAG, String.format("response code:%d", con.getResponseCode()));
				return null;
			}
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
	}
}
