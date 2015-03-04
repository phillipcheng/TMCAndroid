package snae.tmcandroid.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import snae.tmcandroid.util.TMHttpUtil;

public class TMPublicClient {
	
	private static final String TAG="TMPublicClient";
	
	private String server = "http://52.1.96.115:80";
	private String service = "/vol-appserver/public";

	private void initClient(){
		
	}
	
	public TMPublicClient(){
		initClient();
	}
	
	public TMPublicClient(String serverUrl){
		this.server = serverUrl;
		initClient();
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param userName
	 * @return
	 */
	public List<UserBonus> listUserBonus(int tenantId, String userName) {
		String strUrl = String.format("%s%s/bonus/%d?userName=%s", server, service, tenantId, userName);
		String rsp = TMHttpUtil.getContentFromGetURL(strUrl);
		try{
			JSONArray jsarray = new JSONArray(rsp);
			List<UserBonus> ubl = new ArrayList<UserBonus>();
			for (int i=0;i<jsarray.length();i++){
				JSONObject jobj = jsarray.getJSONObject(i);
				ubl.add(UserBonus.fromJSONObject(jobj));
			}
			return ubl;
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
	}
	
	/**
	 * @param tenantId
	 */
	public List<UserPromotion> listPromotions(int tenantId) {
		String strUrl = String.format("%s%s/promotion/%d", server, service, tenantId);
		String rsp = TMHttpUtil.getContentFromGetURL(strUrl);
		try{
			List<UserPromotion> upl = new ArrayList<UserPromotion>();
			JSONArray jsarray = new JSONArray(rsp);
			for (int i=0; i<jsarray.length(); i++){
				JSONObject jobj = jsarray.getJSONObject(i);
				upl.add(UserPromotion.fromJSONObject(jobj));
			}
			return upl;
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
	}
	/**
	 * Get Quota to Check Balance
	 * @param tenantId
	 * @param userName
	 */
	public UserQuota getQuota(int tenantId, String userName) {
		try{
			String strUrl = String.format("%s%s/quota/%d?userName=%s", server, service, tenantId, userName);
			String rsp = TMHttpUtil.getContentFromGetURL(strUrl);
                        if (rsp == null) {
                           Log.w(TAG, "no quota found! " + strUrl);
                          return null;
                        } 

                        JSONArray jsarray = new JSONArray(rsp);
                        if (jsarray.length() < 1) {
                          Log.w(TAG, "quota array empty! " + strUrl);
                          return null;
                        }  
                        return UserQuota.fromJSONObject((JSONObject) jsarray.get(0));
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
	}

	/**
	 * 
	 * @param tenantId
	 * @param promotionId
	 * @param userName
	 * @param userProperties, the user properties passed to be used by Promotion Rule
	 * @return UserBonusResult
	 */
	public UserBonusResult grabBonus(int tenantId, int promotionId, 
			String userName, Map<String, String> userProperties) {
		String strUrl = String.format("%s%s/getbonus/%d", server, service, tenantId);
		String query = String.format("promotionId=%d&userName=%s", promotionId, userName);
		String rsp = TMHttpUtil.getConentFromPostURL(strUrl, query);
		try{
			JSONObject jobj = new JSONObject(rsp);
			return UserBonusResult.fromJSONObject(jobj);
		}catch(Exception e){
			Log.e(TAG, "", e);
			return null;
		}
		
	}
	
	/**
	 * Activate the bonus grabbed
	 * @param tenantId
	 * @param bonusId 
	 * 
	 */
	public boolean activateBonus(int tenantId, long bonusId) {
		try{
			String strUrl = String.format("%s%s/activebonus/%d", server, service, tenantId);
			String query = String.format("bonusId=%d", bonusId);
			String rsp = TMHttpUtil.getConentFromPostURL(strUrl, query);
			try{
				boolean ret = Boolean.parseBoolean(rsp);
				return ret;
			}catch(Exception e){
				Log.e(TAG, "not boolean: rspBody:"+ rsp, e);
				return false;
			}
		}catch(Exception e){
			Log.e(TAG, "", e);
			return false;
		}
	}

}
