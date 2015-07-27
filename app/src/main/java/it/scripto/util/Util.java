package it.scripto.util;

import android.content.Context;

/**
 * Util class
 * @author pincopallino93
 * @version 1.0
 */
public class Util {

    /**
     * Static context
     */
    private static Context context = null;

    /**
     * Method that allow to set context with the actual context
     * @param context the context to be "store"
     */
    public static void setContext(Context context) {
        Util.context = context;
    }

    /**
     * Method that allow to retrieve the context "stored"
     * @return the context "stored"
     */
    public static Context getContext() {
        return Util.context;
    }
}
