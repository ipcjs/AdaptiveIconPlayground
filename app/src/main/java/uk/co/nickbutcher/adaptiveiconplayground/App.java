package uk.co.nickbutcher.adaptiveiconplayground;

import android.app.Application;
import android.content.Context;

/**
 * Created by ipcjs on 07/31.
 */

public class App extends Application {
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }
}
