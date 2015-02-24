package snae.tmcandroid.app;

import org.json.JSONObject;

public class UserPromotion {
	/**
	 * The name.
	 */
	private String name;
	/**
	 * The start time.
	 */
	private long startTime;
	
	/**
	 * The end time.
	 */
	private long endTime;
	
	/**
	 * The rule.
	 */
	private String rule;
	
	public static UserPromotion fromJSONObject(JSONObject jobj){
		UserPromotion ub = new UserPromotion();
		return ub;
	}
}
