package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;

import java.util.EnumSet;

import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_PRESSURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_HEART_RATE;

/**
 * Created by mindwing on 2016-01-24.
 */
public class ConnectionManager implements Application.ActivityLifecycleCallbacks {
    private static String TAG = "ConnectionManager";
    ResourceName lastConnectedResource;
    Activity activity;

    void connectToServer(ResourceName resource) {
        Util.toast(activity, "trying to connect to server...");

        disconnectFromServer(lastConnectedResource);
        lastConnectedResource = null;

        try {
            switch (resource) {
                case SPO2:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_SPO2,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            (OcPlatform.OnResourceFoundListener) activity
                    );
                    break;

                case HEART_RATE:
                    OcPlatform.findResource("",
                            QUERY_HEART_RATE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            (OcPlatform.OnResourceFoundListener) activity
                    );

                case BLOOD_PRESSURE:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_PRESSURE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            (OcPlatform.OnResourceFoundListener) activity
                    );
                    break;

                case BODY_TEMPERATURE:
                    OcPlatform.findResource("",
                            QUERY_BODY_TEMPERATURE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            (OcPlatform.OnResourceFoundListener) activity
                    );
                    break;

                case BLOOD_GLUCOSE:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_GLUCOSE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            (OcPlatform.OnResourceFoundListener) activity
                    );
                    break;

            }
        } catch (OcException e) {
            Util.toast(activity, e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    // stop observation
    void disconnectFromServer(ResourceName resource) {
        if (resource == null) {
            return;
        }

        // TODO Observation 취소할 것
    }

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
}
