package snae.tmcandroid.test;

import android.util.Log;

import junit.framework.TestCase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import snae.tmcandroid.traffic.TMManager;
import snae.tmcandroid.util.TMHttpUtil;

public class Test1 extends TestCase{
	private static final String TAG="Test1";
	private static final String url1 = "http://www.ebay.com/";
	
	private String user="123";
	private String tenantId = "1";
	
	public void testStopSession(){
		TMManager tmMgr = new TMManager();
		tmMgr.end();
	}
	
	public void testStartSession(){
		TMManager tmMgr = new TMManager();
		tmMgr.start(user, tenantId);
	}
	
	public void test1(){
		
		Log.i(TAG, "hello");
		int tenantId = 1;
		int promotionId = 2;
		String userName = "123";

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

        TMManager tmMgr = new TMManager();
        boolean ret = tmMgr.start(userName, tenantId + "");
        if (ret) {
            try {
                int i = 0;
                while (i < 5) {
                    i++;
                    //
                    URL url = new URL(url1);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int statusCode = con.getResponseCode();
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        String rspBody = TMHttpUtil.getStringFromInputStream(is);
                        is.close();
                        if (rspBody != null) {
                            Log.i(TAG, String.format("status code is %d for getting url:%s, len:%d",
                                    statusCode, url1, rspBody.length()));
                        } else {
                            Log.e(TAG, "rsp is null.");
                        }
                    } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Log.i(TAG, "no more balance.");
                        break;
                    } else {
                        Log.i(TAG, String.format("status code got %d.", statusCode));
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
            tmMgr.end();
        } else {
            int rejectId = tmMgr.getRejectReasonId();
        }
    }
}