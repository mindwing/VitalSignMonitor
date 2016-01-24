package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by mindwing on 2016-01-24.
 */
public class Util {
    static void toast(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
