package snae.tmcandroid.app;

import org.json.JSONObject;

import android.util.Log;

public class UserQuota {
	private static final String TAG="TMPublicClient";
	
	/**
	 * The balance.
	 */
	private long balance;
	private static final String JSON_BALANCE="balance";
	
	/**
	 * The activation time.
	 */
	private long activationTime;
	
	/**
	 * The expiration time.
	 */
	private long expirationTime;

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public long getActivationTime() {
		return activationTime;
	}

	public void setActivationTime(long activationTime) {
		this.activationTime = activationTime;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	public static UserQuota fromJSONObject(JSONObject jobj){
		UserQuota uq = new UserQuota();
		try{
			uq.setBalance(jobj.getLong(JSON_BALANCE));
		}catch(Exception e){
			Log.e(TAG, "", e);
		}
		return uq;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(JSON_BALANCE+ ":" + this.getBalance());
		return sb.toString();
	}
}
