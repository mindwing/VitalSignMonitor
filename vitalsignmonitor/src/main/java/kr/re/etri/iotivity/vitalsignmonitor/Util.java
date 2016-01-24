package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by mindwing on 2016-01-24.
 */
public class Util {
    private static Activity activity;

    static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity _activity, Bundle savedInstanceState) {
            activity = _activity;
        }

        @Override
        public void onActivityStarted(Activity _activity) {
            activity = _activity;
        }

        @Override
        public void onActivityResumed(Activity _activity) {
            activity = _activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

    static void toast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    static void runOnCurrentUiThread(Runnable runnable) {
        activity.runOnUiThread(runnable);
    }
}
