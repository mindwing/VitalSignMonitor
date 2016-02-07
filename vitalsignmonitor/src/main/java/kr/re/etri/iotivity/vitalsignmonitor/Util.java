package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import kr.re.etri.iotivity.smartwearable.Utils;
import kr.re.etri.iotivity.smartwearable.Utils.UtilTrait;

/**
 * 편의메서드를 담는 클래스
 */
public class Util implements UtilTrait {
    static TextView logView;
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
        public void onActivityPaused(Activity _activity) {
            activity = null;
        }

        @Override
        public void onActivityStopped(Activity _activity) {
            activity = null;
        }

        @Override
        public void onActivitySaveInstanceState(Activity _activity, Bundle outState) {
            activity = null;
        }

        @Override
        public void onActivityDestroyed(Activity _activity) {
            activity = null;
        }
    };

    static {
        Utils.setLoggable(new Util());
    }

    static void setup(TextView _logView) {
        logView = _logView;
        logView.append("=== Vital Sign Monitor started.\n");
    }

    public void toast(final String msg) {
        final Activity act = activity;

        if (act == null) {
            return;
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(act, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void runOnCurrentUiThread(Runnable runnable) {
        final Activity act = activity;

        if (act == null) {
            return;
        }

        act.runOnUiThread(runnable);
    }

    public void log(final String msg) {
        if (logView != null) {
            logView.post(new Runnable() {
                public void run() {
                    logView.append("\n> ");
                    logView.append(msg);
                    Log.d("Monitor", msg);
                }
            });
        }
    }
}
