package snae.tmcandroid.app;

import android.util.Log;

import org.json.JSONObject;

/**
 * @version 1.0.0
 */
public class UserBonusResult {
    private static final String TAG = "TMPublicClient";

    private int code;
    public static final String JSON_CODE = "code";

    private String message;
    public static final String JSON_MESSAGE = "message";

    private UserBonus bonus;
    public static final String JSON_BONUS = "bonus";

    public static UserBonusResult fromJSONObject(JSONObject jobj) {
        UserBonusResult ubr = new UserBonusResult();
        try {
            ubr.setCode(jobj.getInt(JSON_CODE));
            ubr.setMessage(jobj.getString(JSON_MESSAGE));
            if (jobj.has(JSON_BONUS)) {
                JSONObject ub = jobj.getJSONObject(JSON_BONUS);
                if (ub != null) {
                    ubr.setBonus(UserBonus.fromJSONObject(ub));
                } else {
                    Log.w(TAG, JSON_BONUS + " in result is null");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return ubr;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserBonus getBonus() {
        return bonus;
    }

    public void setBonus(UserBonus bonus) {
        this.bonus = bonus;
    }
}
