package com.communitycode.amps.main.settings;

import android.content.Context;
import android.util.Log;

import com.communitycode.amps.main.battery.BatteryMethodInterface;
import com.communitycode.amps.main.battery.OfficialBatteryMethod;
import com.communitycode.amps.main.battery.UnofficialBatteryMethod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BatteryMethodPicker {
    public static String DISCHARGEFIELD = "DISCHARGEFIELD";
    public static String CHARGEFIELD = "CHARGEFIELD";
    public static String FILEPATH = "FILEPATH";
    public static String SCALE = "SCALE";
    public static String READER = "READER";
    public static String TYPE = "TYPE";
    public static String OFFICIALBATTERYMETHOD = "OFFICIALBATTERYMETHOD";
    public static String UNOFFICIALBATTERYMETHOD = "UNOFFICIALBATTERYMETHOD";

    public static BatteryMethodInterface fromJson(String json, Context mCtx) {
        if (json == null) {
            return null;
        }

        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            String type = jsonObject.get(TYPE).getAsString();
            if (type.equals(OFFICIALBATTERYMETHOD)) {
                return new OfficialBatteryMethod(mCtx);
            }
            if (type.equals(UNOFFICIALBATTERYMETHOD)) {
                return new UnofficialBatteryMethod(
                        jsonObject.get(READER).getAsInt(),
                        jsonObject.get(FILEPATH).getAsString(),
                        jsonObject.get(SCALE).getAsFloat(),
                        jsonObject.has(DISCHARGEFIELD) ? jsonObject.get(DISCHARGEFIELD).getAsString() : null,
                        jsonObject.has(CHARGEFIELD) ? jsonObject.get(CHARGEFIELD).getAsString() : null,
                        new String[]{});
            }
            Log.d("Amps", "Unknown method. Json=" + json);
        } catch (Exception e) {
            Log.d("Amps", "Failed to parse preference. Json=" + json + " error=" + e.getMessage());
        }
        return null;
    }

    public static String toJson(BatteryMethodInterface obj) {
        if (obj instanceof OfficialBatteryMethod) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(TYPE, OFFICIALBATTERYMETHOD);
            Gson gson = new Gson();
            return gson.toJson(jsonObject);
        }
        if (obj instanceof UnofficialBatteryMethod) {
            UnofficialBatteryMethod method = (UnofficialBatteryMethod) obj;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(TYPE, UNOFFICIALBATTERYMETHOD);
            jsonObject.addProperty(DISCHARGEFIELD, method.dischargeField);
            jsonObject.addProperty(CHARGEFIELD, method.chargeField);
            jsonObject.addProperty(FILEPATH, method.filePath);
            jsonObject.addProperty(READER, method.reader);
            jsonObject.addProperty(SCALE, method.scale);
            Gson gson = new Gson();
            return gson.toJson(jsonObject);
        }

        return null;
    }
}
