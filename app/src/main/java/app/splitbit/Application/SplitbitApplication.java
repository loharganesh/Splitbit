package app.splitbit.Application;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.FirebaseApp;


public class SplitbitApplication extends Application {

    private static SplitbitApplication mInstance;
    protected static boolean isVisible = false;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);



    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public static Context getInstance() {
        return mInstance;
    }

}
