package snae.tmcandroid.app;

import android.util.Log;

import org.json.JSONObject;

/**
 * 
 * @version 1.0.0
 *
 */
public class UserPromotion {
    private static final String TAG="TMPublicClient";

    /**
     * The id.
     */
    private long id;
    public static final String JSON_ID = "id";

    /**
	 * The name.
	 */
	private String name;
    public static final String JSON_NAME = "name";

    /**
	 * The start time.
	 */
	private long startTime;
    public static final String JSON_START_TIME = "startTime";

    /**
	 * The end time.
	 */
	private long endTime;
    public static final String JSON_END_TIME = "endTime";

    /**
     * The rule.
     */
    private String rule;
    public static final String JSON_RULE = "rule";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
	
	public static UserPromotion fromJSONObject(JSONObject jobj){
		UserPromotion ub = new UserPromotion();
        try{
            ub.setId(jobj.getLong(JSON_ID));
            ub.setName(jobj.getString(JSON_NAME));
            ub.setStartTime(jobj.getLong(JSON_START_TIME));
            ub.setEndTime(jobj.getLong(JSON_END_TIME));
            ub.setRule(jobj.getString(JSON_RULE));
        }catch(Exception e){
            Log.e(TAG, "", e);
        }
		return ub;
	}

}
