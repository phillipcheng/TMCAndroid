package snae.tmcandroid.app;

import org.json.JSONObject;

import android.util.Log;

public class UserBonus {
	private static final String TAG="TMPublicClient";
	
	/**
	 * The id.
	 */
	private long id;
	public static final String JSON_ID="id";
	
	/**
	 * The size.
	 */
	private long size;
	public static final String JSON_SIZE="size";
	
	/**
	 * The activation time.
	 */
	private long activationTime;
	public static final String JSON_ACTIVATIONTIME="activationTime";
	
	/**
	 * 
	 */
	private long expirationTime;
	public static final String JSON_EXPIRATIONTIME="expirationTime";
	
	/**
	 * The promotion id.
	 */
	private int promotionId;
	public static final String JSON_PROMOTIONID="promotionId";

	
	public static UserBonus fromJSONObject(JSONObject jobj){
		UserBonus ub = new UserBonus();
		try{
			ub.setId(jobj.getLong(JSON_ID));
			ub.setSize(jobj.getLong(JSON_SIZE));
			ub.setActivationTime(jobj.getLong(JSON_ACTIVATIONTIME));
			ub.setExpirationTime(jobj.getLong(JSON_EXPIRATIONTIME));
			ub.setPromotionId(jobj.getInt(JSON_PROMOTIONID));
		}catch(Exception e){
			Log.e(TAG, "", e);
		}
		return ub;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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

	public int getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(int promotionId) {
		this.promotionId = promotionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
