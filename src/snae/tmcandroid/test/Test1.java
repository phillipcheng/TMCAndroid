package snae.tmcandroid.test;

import java.io.InputStream;
import java.net.HttpURLConnection;

import junit.framework.TestCase;

import snae.tmcandroid.app.TMPublicClient;
import snae.tmcandroid.app.UserBonus;
import snae.tmcandroid.app.UserBonusResult;
import snae.tmcandroid.app.UserQuota;
import snae.tmcandroid.traffic.TMURL;
import snae.tmcandroid.traffic.TMURLManager;
import snae.tmcandroid.util.TMHttpUtil;
import android.util.Log;

public class Test1 extends TestCase{
	private static final String TAG="Test1";
	private static final String url1 = "http://www.ebay.com/";
	
	public void test1(){
		
		Log.i(TAG, "hello");
		int tenantId = 3;
		int promotionId = 2;
		String userName = "1234";
//		TMPublicClient publicClient = new TMPublicClient();
//		UserQuota uq = publicClient.getQuota(tenantId, userId);
//		Log.i(TAG, String.format("user quota:%s", uq.toString()));
//		
//		UserBonusResult ubr = publicClient.grabBonus(tenantId, promotionId, userId, null);
//		UserBonus ub = ubr.getBonus();
//		if (ub!=null){
//			boolean ret = publicClient.activateBonus(tenantId, ub.getId());
//			uq = publicClient.getQuota(tenantId, userId);
//			Log.i(TAG, String.format("user quota:%s", uq.toString()));
//		}
		
		TMURLManager tmMgr = new TMURLManager();
		boolean ret = tmMgr.start(userName, tenantId+"");
		if (ret){
			try{
				int i=0;
				while(i<5){
					i++;
					//
					TMURL tmUrl = tmMgr.getUrl(url1);
					HttpURLConnection con = tmUrl.getHttpUrlConnection();
					int statusCode = con.getResponseCode();
					if (statusCode == HttpURLConnection.HTTP_OK){
						InputStream is = con.getInputStream();
						String rspBody = TMHttpUtil.getStringFromInputStream(is);
						is.close();
						if (rspBody!=null){
							Log.i(TAG, String.format("status code is %d for getting url:%s, len:%d", 
								statusCode, url1, rspBody.length()));
						}else{
							Log.e(TAG, "rsp is null.");
						}
					}else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
						Log.i(TAG, "no more balance.");
						break;
					}else{
						Log.i(TAG, String.format("status code got %d.", statusCode));
						break;
					}
				}
			}catch(Exception e){
				Log.e(TAG, "", e);
			}
			tmMgr.end();
		}else{
			int rejectId = tmMgr.getRejectReasonId();
		}
	}
}