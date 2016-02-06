package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import kr.re.etri.iotivity.smartwearable.ConnectionManager;
import kr.re.etri.iotivity.smartwearable.ResourceName;

/**
 * Vital Sign 을 모니터링하는 액티비티
 */
public class MonitorActivity extends AppCompatActivity {

    private static final String TAG = MonitorActivity.class.getSimpleName();

    private ConnectionManager connManager = new ConnectionManager();

    private TextView updateDate;

    private TextView[] spo2View = new TextView[4];
    private TextView[] heartRateView = new TextView[4];
    private TextView[] bloodPressureView = new TextView[4];
    private TextView[] bodyTemperatureView = new TextView[4];
    private TextView[] bloodGlucoseView = new TextView[4];

    /**
     * 필드들의 갱신시각을 화면에 표시
     */
    private class UpdateDateRunner implements Runnable, Observer {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        @Override
        public void run() {
            String date = dateFormat.format(new Date());
            updateDate.setText(date);
        }

        @Override
        public void update(Observable observable, Object data) {
            updateDate.post(this);
        }
    }

    private UpdateDateRunner updateDateRunner = new UpdateDateRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application app = getApplication();
        app.registerActivityLifecycleCallbacks(Util.lifecycleCallbacks);

        setContentView(R.layout.activity_monitor);

        setupView();
        connManager.setup(updateDateRunner, spo2View, heartRateView, bloodPressureView, bodyTemperatureView, bloodGlucoseView);

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

    /**
     * server 에 연결하고자 버튼을 터치하면 호출되는 메서드
     * @param view 터치한 View
     */
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
