package kr.re.etri.iotivity.vitalsignserver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

public class ServerActivity extends Activity {

    private static final String TAG = ServerActivity.class.getSimpleName();

    private BloodGlucoseResource bloodGlucoseResource;
    private BloodPressureResource bloodPressureResource;
    private BloodSpO2Resource bloodSpO2Resource;
    private BodyTemperatureResource bodyTemperatureResource;
    private HeartRateResource heartRateResource;

    //
    // BEGIN
    // Activity Part
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }

    private void toast(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void toggleServer(View view) {
        ToggleButton toggle = (ToggleButton) view;

        if (toggle.isChecked()) {
            startServer();
        } else {
            stopServer();
        }
    }

    //
    // BEGIN
    // IoTivity Part
    //

    private void startServer() {
        PlatformConfig platformConfig = new PlatformConfig(
                this,
                ServiceType.IN_PROC,
                ModeType.SERVER,
                "0.0.0.0", // By setting to "0.0.0.0", it binds to all available interfaces
                0,         // Uses randomly available port
                QualityOfService.LOW
        );

        Log.d(TAG, "Configuring platform.");
        OcPlatform.Configure(platformConfig);

        createNewResources();

        Log.d(TAG, "Waiting for the requests...");
    }

    private void createNewResources() {
        bloodPressureResource = new BloodPressureResource("my_blood_pressure");
        bloodGlucoseResource = new BloodGlucoseResource("my_blood_glucose");
        bloodSpO2Resource = new BloodSpO2Resource("my_blood_spo2");
        bodyTemperatureResource = new BodyTemperatureResource("my_heart_rate");
        heartRateResource = new HeartRateResource("my_heart_rate");

        try {
            bloodPressureResource.registerResource();
            bloodGlucoseResource.registerResource();
            bloodSpO2Resource.registerResource();
            bodyTemperatureResource.registerResource();
            heartRateResource.registerResource();
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to register healthcare resources");
        }
    }

    private void stopServer() {
        // TODO observation 정리하기
    }
}
