package it.scripto.util;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import it.scripto.primecalculator.BuildConfig;

/**
 * Base actionbar activity with logging.
 * @author pincopallino93
 * @version 1.1
 */
public abstract class BaseActivity extends ActionBarActivity {

    public final String TAG = getTagLog();

    public String getTagLog() {
        return getClass().getCanonicalName();
    }

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResourceId());

        // Set context for usage in generic class outside Android
        Util.setContext(this);

        if (BuildConfig.DEBUG) Log.v(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Log.v(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (BuildConfig.DEBUG) Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.v(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Log.v(TAG, "onDestroy");
    }
}
