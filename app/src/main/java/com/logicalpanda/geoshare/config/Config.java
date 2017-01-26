package com.logicalpanda.geoshare.config;

import android.content.Context;

/**
 * Created by Ger on 24/07/2016.
 */
public class Config {
    public static String restUrl;
    public static String restKey;
    public static String distanceToRetrieve;

    public static void SetupConfig(Context context)
    {
        Config.restUrl = ConfigHelper.getConfigValue(context, "rest_url");
        Config.restKey = ConfigHelper.getConfigValue(context, "rest_key");
        Config.distanceToRetrieve = ConfigHelper.getConfigValue(context, "distanceToRetrieve");
    }

}
