package com.omerucel.oauthdialog;

import org.json.JSONException;
import org.json.JSONObject;

public class AccessToken {
    private JSONObject jsonObject;

    public AccessToken(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getTokenType() throws JSONException {
        return jsonObject.getString("token_type");
    }

    public String getAccessToken() throws JSONException {
        return jsonObject.getString("access_token");
    }

    public String getRefreshToken() throws JSONException {
        return jsonObject.getString("refresh_token");
    }

    public int getExpiresIn() throws JSONException {
        return jsonObject.getInt("expires_in");
    }
}
