package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_PRESSURE_DIASTOLIC;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_PRESSURE_SYSTOLIC;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.KEY_HEART_RATE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_PRESSURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.QUERY_HEART_RATE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_GLUCOSE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_PRESSURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BLOOD_SPO2;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_BODY_TEMPERATURE;
import static kr.re.etri.iotivity.vitalsignmonitor.VitalSignResource.URI_HEART_RATE;

public class MonitorActivity extends Activity implements
        OcPlatform.OnResourceFoundListener,
        OcResource.OnGetListener {

    private static final String TAG = MonitorActivity.class.getSimpleName();

    private OcResource foundBloodGlucoseResource;
    private OcResource foundBloodPressureResource;
    private OcResource foundBloodSpO2Resource;
    private OcResource foundBodyTemperatureResource;
    private OcResource foundHeartRateResource;

    private TextView[] spo2View = new TextView[4];
    private TextView[] heartRateView = new TextView[4];
    private TextView[] systolicView = new TextView[4];
    private TextView[] diastolicView = new TextView[4];
    private TextView[] bodyTemperatureView = new TextView[4];
    private TextView[] glucoseView = new TextView[4];

    private static final int GATHERING_PER_10SEC = 0x1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == GATHERING_PER_10SEC) {
            }
        }
    };

    //
    // BEGIN
    // Activity Part
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        _toast("1: " + getBaseContext().toString());
//        _toast("2: " + getApplicationContext().toString());
//        _toast("3: " + toString());

        super.onCreate(savedInstanceState);

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

        systolicView[0] = (TextView) findViewById(R.id.systolic);
        systolicView[1] = (TextView) findViewById(R.id.systolic_1);
        systolicView[2] = (TextView) findViewById(R.id.systolic_2);
        systolicView[3] = (TextView) findViewById(R.id.systolic_3);

        diastolicView[0] = (TextView) findViewById(R.id.diastolic);
        diastolicView[1] = (TextView) findViewById(R.id.diastolic_1);
        diastolicView[2] = (TextView) findViewById(R.id.diastolic_2);
        diastolicView[3] = (TextView) findViewById(R.id.diastolic_3);

        bodyTemperatureView[0] = (TextView) findViewById(R.id.body_temp);
        bodyTemperatureView[1] = (TextView) findViewById(R.id.body_temp_1);
        bodyTemperatureView[2] = (TextView) findViewById(R.id.body_temp_2);
        bodyTemperatureView[3] = (TextView) findViewById(R.id.body_temp_3);

        glucoseView[0] = (TextView) findViewById(R.id.glucose);
        glucoseView[1] = (TextView) findViewById(R.id.glucose_1);
        glucoseView[2] = (TextView) findViewById(R.id.glucose_2);
        glucoseView[3] = (TextView) findViewById(R.id.glucose_3);
    }

    public void toggleConnection(View view) {
        if (((ToggleButton) view).isChecked()) {
            connectToServer();
        } else {
            clearView();
            disconnectFromServer();
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

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

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

    private void connectToServer() {
        toast("trying to connect to server...");

        try {
            OcPlatform.findResource("",
                    QUERY_BLOOD_GLUCOSE,
                    EnumSet.of(OcConnectivityType.CT_DEFAULT),
                    this
            );

            OcPlatform.findResource("",
                    QUERY_BLOOD_PRESSURE,
                    EnumSet.of(OcConnectivityType.CT_DEFAULT),
                    this
            );

            OcPlatform.findResource("",
                    QUERY_BLOOD_SPO2,
                    EnumSet.of(OcConnectivityType.CT_DEFAULT),
                    this
            );

            OcPlatform.findResource("",
                    QUERY_BODY_TEMPERATURE,
                    EnumSet.of(OcConnectivityType.CT_DEFAULT),
                    this
            );

            OcPlatform.findResource("",
                    QUERY_HEART_RATE,
                    EnumSet.of(OcConnectivityType.CT_DEFAULT),
                    this
            );
        } catch (OcException e) {
            toast(e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    // stop observation
    private void disconnectFromServer() {
        // TODO Observation 취소할 것
    }

    @Override
    public void onResourceFound(OcResource ocResource) {
        if (null == ocResource) {
            toast("onResourceFound():ocResource is null");
            Log.e(TAG, "ocResource is null");

            return;
        }

        // Get the resource URI
        String resourceUri = ocResource.getUri();
        toast("onResourceFound(): resourceUri: " + resourceUri);
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
        toast("Getting Blood Glucose Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodGlucoseResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodPressureResourceRepresentation() {
        toast("Getting Blood Pressure Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodPressureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodSpO2ResourceRepresentation() {
        toast("Getting Blood SpO2 Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodSpO2Resource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBodyTemperatureResourceRepresentation() {
        toast("Getting Body Temperature Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBodyTemperatureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getHeartBeatResourceRepresentation() {
        toast("Getting HeartBeat Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundHeartRateResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            toast("Error occurred while invoking \"get\" API");
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
                            glucoseView[0].setText(ocRepresentation.getValue(KEY_BLOOD_GLUCOSE).toString());
                            break;

                        case URI_BLOOD_PRESSURE:
                            systolicView[0].setText(ocRepresentation.getValue(KEY_BLOOD_PRESSURE_SYSTOLIC).toString());
                            diastolicView[0].setText(ocRepresentation.getValue(KEY_BLOOD_PRESSURE_DIASTOLIC).toString());
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
    public void onGetFailed(Throwable throwable) {
        toast(throwable.getMessage());
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    private void clearView() {
        spo2View[0].setText("N/A");
        heartRateView[0].setText("N/A");
        systolicView[0].setText("N/A");
        diastolicView[0].setText("N/A");
        bodyTemperatureView[0].setText("N/A");
        glucoseView[0].setText("N/A");
    }
}
