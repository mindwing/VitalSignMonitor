package kr.re.etri.iotivity.smartwearable;

/**
 * Created by mindwing on 2016-02-07.
 */

public class Utils {

    private static UtilTrait util;

    public static void setLoggable(UtilTrait _util) {
        util = _util;
    }

    public static void toast(final String msg) {
        util.toast(msg);
    }

    public static void runOnCurrentUiThread(Runnable runnable) {
        util.runOnCurrentUiThread(runnable);
    }

    public static void log(String msg) {
        util.log(msg);
    }

    public interface UtilTrait {
        void toast(final String msg);

        void runOnCurrentUiThread(Runnable runnable);

        void log(String msg);
    }
}
