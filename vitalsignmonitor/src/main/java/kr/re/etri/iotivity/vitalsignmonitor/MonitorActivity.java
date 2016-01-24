package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.iotivity.base.ModeType;
import org.iotivity.base.ObserveType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_PRESSURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_HEART_RATE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_PRESSURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_HEART_RATE;

public class MonitorActivity extends Activity implements
        OcPlatform.OnResourceFoundListener,
        OcResource.OnGetListener {

    private static final String TAG = MonitorActivity.class.getSimpleName();
    private static final int GATHERING_PER_10SEC = 0x1;

    private OcResource foundBloodGlucoseResource;
    private OcResource foundBloodPressureResource;
    private OcResource foundBloodSpO2Resource;
    private OcResource foundBodyTemperatureResource;
    private OcResource foundHeartRateResource;

    private BloodGlucoseData bloodGlucoseData;

    private TextView[] spo2View = new TextView[4];
    private TextView[] heartRateView = new TextView[4];
    private TextView[] bloodPressureView = new TextView[4];
    private TextView[] bodyTemperatureView = new TextView[4];
    private TextView[] bloodGlucoseView = new TextView[4];

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == GATHERING_PER_10SEC) {
            }
        }
    };

    ConnectionManager connManager = new ConnectionManager();

    //
    // BEGIN
    // Activity Part
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application app = getApplication();
        app.registerActivityLifecycleCallbacks(connManager);

        setContentView(R.layout.activity_monitor);

        setupView();

        prepareConfiguration();
    }

    private void setupView() {
        spo2View[0] = (TextView) findViewById(R.id.spo2);
        spo2View[1] = (TextView) findViewById(R.id.spo2_1);
        spo2View[2] = (TextView) findViewById(R.id.spo2_2);
        spo2View[3] = (TextView) findViewById(R.id.spo2_3);

        heartRateView[0] = (TextView) findViewById(R.id.heart_rate);
        heartRateView[1] = (TextView) findViewById(R.id.heart_rate_1);
        heartRateView[2] = (TextView) findViewById(R.id.heart_rate_2);
        heartRateView[3] = (TextView) findViewById(R.id.heart_rate_3);

        bloodPressureView[0] = (TextView) findViewById(R.id.blood_pressure);
        bloodPressureView[1] = (TextView) findViewById(R.id.blood_pressure_1);
        bloodPressureView[2] = (TextView) findViewById(R.id.blood_pressure_2);
        bloodPressureView[3] = (TextView) findViewById(R.id.blood_pressure_3);

        bodyTemperatureView[0] = (TextView) findViewById(R.id.body_temp);
        bodyTemperatureView[1] = (TextView) findViewById(R.id.body_temp_1);
        bodyTemperatureView[2] = (TextView) findViewById(R.id.body_temp_2);
        bodyTemperatureView[3] = (TextView) findViewById(R.id.body_temp_3);

        bloodGlucoseView[0] = (TextView) findViewById(R.id.blood_glucose);
        bloodGlucoseView[1] = (TextView) findViewById(R.id.blood_glucose_1);
        bloodGlucoseView[2] = (TextView) findViewById(R.id.blood_glucose_2);
        bloodGlucoseView[3] = (TextView) findViewById(R.id.blood_glucose_3);
        bloodGlucoseData = new BloodGlucoseData(bloodGlucoseView[0]);
    }

    public void toggleConnection(View view) {
        String tag = view.getTag().toString();
        ResourceName resourceName= ResourceName.valueOf(tag);

        if (((ToggleButton) view).isChecked()) {
            connManager.connectToServer(resourceName);
        } else {
            clearView();
            connManager.disconnectFromServer(resourceName);
        }
    }

//    private void _toast(final String msg) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });
//
//        Log.e(TAG, "_toast: " + msg);
//    }

    //
    // BEGIN
    // IoTivity Part
    //

    private void prepareConfiguration() {
        PlatformConfig platformConfig = new PlatformConfig(
                this,
                ServiceType.IN_PROC,
                ModeType.CLIENT,
                "0.0.0.0", // By setting to "0.0.0.0", it binds to all available interfaces
                0,         // Uses randomly available port
                QualityOfService.LOW
        );

        OcPlatform.Configure(platformConfig);
    }

    @Override
    public synchronized void onResourceFound(OcResource ocResource) {
        if (null == ocResource) {
            Util.toast(this, "onResourceFound():ocResource is null");
            Log.e(TAG, "ocResource is null");

            return;
        }

        // Get the resource URI
        String resourceUri = ocResource.getUri();
        Util.toast(this, "onResourceFound(): resourceUri: " + resourceUri);
        // Get the resource host address
//        String hostAddress = ocResource.getHost();
//        toast("onResourceFound(): hostAddress: " + hostAddress);

//        for (String resourceType : ocResource.getResourceTypes()) {
//            toast("onResourceFound(): resourceType: " + resourceType);
//        }

//        for (String resourceInterface : ocResource.getResourceInterfaces()) {
//            toast("onResourceFound(): resourceInterface: " + resourceInterface);
//        }

//        for (OcConnectivityType connectivityType : ocResource.getConnectivityTypeSet()) {
//            toast("onResourceFound(): connectivityType: " + connectivityType);
//        }

        switch (resourceUri) {
            case URI_BLOOD_GLUCOSE:
                foundBloodGlucoseResource = ocResource;
                getBloodGlucoseResourceRepresentation();
                break;

            case URI_BLOOD_PRESSURE:
                foundBloodPressureResource = ocResource;
                getBloodPressureResourceRepresentation();
                break;

            case URI_BLOOD_SPO2:
                foundBloodSpO2Resource = ocResource;
                getBloodSpO2ResourceRepresentation();
                break;

            case URI_BODY_TEMPERATURE:
                foundBodyTemperatureResource = ocResource;
                getBodyTemperatureResourceRepresentation();
                break;

            case URI_HEART_RATE:
                foundHeartRateResource = ocResource;
                getHeartBeatResourceRepresentation();
                break;

            default:
                break;
        }
    }

    private void getBloodGlucoseResourceRepresentation() {
        Util.toast(this, "Getting Blood Glucose Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodGlucoseResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast(this, "Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodPressureResourceRepresentation() {
        Util.toast(this, "Getting Blood Pressure Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodPressureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast(this, "Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodSpO2ResourceRepresentation() {
        Util.toast(this, "Getting Blood SpO2 Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodSpO2Resource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast(this, "Error occurred while invoking \"get\" API");
        }
    }

    private void getBodyTemperatureResourceRepresentation() {
        Util.toast(this, "Getting Body Temperature Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBodyTemperatureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast(this, "Error occurred while invoking \"get\" API");
        }
    }

    private void getHeartBeatResourceRepresentation() {
        Util.toast(this, "Getting HeartBeat Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundHeartRateResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast(this, "Error occurred while invoking \"get\" API");
        }
    }

    @Override
    public void onGetCompleted(List<OcHeaderOption> list, final OcRepresentation ocRepresentation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String resourceUri = ocRepresentation.getUri();
                Log.w(TAG, "getUri(): " + ocRepresentation.getUri());

                try {
                    switch (resourceUri) {
                        case URI_BLOOD_GLUCOSE:
                            bloodGlucoseView[0].setText(ocRepresentation.getValue(KEY_BLOOD_GLUCOSE).toString());
                            SystemClock.sleep(1);

                            foundBloodGlucoseResource.observe(
                                    ObserveType.OBSERVE,
                                    new HashMap<String, String>(),
                                    bloodGlucoseData);
                            break;

                        case URI_BLOOD_PRESSURE:
                            bloodPressureView[0].setText(ocRepresentation.getValue(KEY_BLOOD_PRESSURE).toString());
                            break;

                        case URI_BLOOD_SPO2:
                            spo2View[0].setText(ocRepresentation.getValue(KEY_BLOOD_SPO2).toString());
                            break;

                        case URI_BODY_TEMPERATURE:
                            bodyTemperatureView[0].setText(ocRepresentation.getValue(KEY_BODY_TEMPERATURE).toString());
                            break;

                        case URI_HEART_RATE:
                            heartRateView[0].setText(ocRepresentation.getValue(KEY_HEART_RATE).toString());
                            break;

                        default:
                            break;
                    }
                } catch (OcException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public synchronized void onGetFailed(Throwable throwable) {
        Util.toast(this, throwable.getMessage());
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    private void clearView() {
        spo2View[0].setText("N/A");
        heartRateView[0].setText("N/A");
        bloodPressureView[0].setText("N/A");
        bodyTemperatureView[0].setText("N/A");
        bloodGlucoseView[0].setText("N/A");
    }
}
