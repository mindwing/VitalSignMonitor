package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

public class MonitorActivity extends Activity {

    private static final String TAG = MonitorActivity.class.getSimpleName();
    private static final int GATHERING_PER_10SEC = 0x1;

    private ConnectionManager connManager = new ConnectionManager();

    private TextView updateDate;

    private TextView[] spo2View = new TextView[4];
    private TextView[] heartRateView = new TextView[4];
    private TextView[] bloodPressureView = new TextView[4];
    private TextView[] bodyTemperatureView = new TextView[4];
    private TextView[] bloodGlucoseView = new TextView[4];

    //
    // BEGIN
    // Activity Part
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application app = getApplication();
        app.registerActivityLifecycleCallbacks(Util.lifecycleCallbacks);

        setContentView(R.layout.activity_monitor);

        setupView();
        connManager.setup(updateDate, spo2View[0], heartRateView[0], bloodPressureView[0], bodyTemperatureView[0], bloodGlucoseView[0]);

        prepareConfiguration();
    }

    private void setupView() {
        updateDate = (TextView) findViewById(R.id.update_date);

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
    }

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

    public void toggleConnection(View view) {
        String tag = view.getTag().toString();
        ResourceName resourceName = ResourceName.valueOf(tag);

        if (((ToggleButton) view).isChecked()) {
            connManager.connectToServer(resourceName);
        } else {
            connManager.disconnectFromServer(resourceName);
        }
    }
}
