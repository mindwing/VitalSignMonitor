package kr.re.etri.iotivity.vitalsignmonitor;

import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.ObserveType;
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mindwing on 2016-01-24.
 */
public class ConnectionManager implements
        Observer,
        HealthCareResourceSpec,
        OcPlatform.OnResourceFoundListener,
        OcResource.OnGetListener {
    private static String TAG = "ConnectionManager";

    private TextView updateDate;
    Runnable updateDateRunner = new Runnable() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        @Override
        public void run() {
            String date = dateFormat.format(new Date());
            updateDate.setText(date);
        }
    };
    private OcResource foundBloodSpO2Resource;
    private OcResource foundHeartRateResource;
    private OcResource foundBloodPressureResource;
    private OcResource foundBodyTemperatureResource;
    private OcResource foundBloodGlucoseResource;
    private BloodSpO2ObservableData bloodSpO2Data;
    private HeartRateObservableData heartRateObservableData;
    private BloodPressureObservableData bloodPressureObservableData;
    private BodyTemperatureObservableData bodyTemperatureObservableData;
    private BloodGlucoseObservableData bloodGlucoseData;
    private ResourceName lastConnectedResource;

    void setup(TextView _updateDate, TextView _spo2View, TextView _heartRateView, TextView _bloodPressureView,
               TextView _bodyTemperatureView, TextView _bloodGlucoseView) {
        updateDate = _updateDate;

        bloodSpO2Data = new BloodSpO2ObservableData(_spo2View, this);
        heartRateObservableData = new HeartRateObservableData(_heartRateView, this);
        bloodPressureObservableData = new BloodPressureObservableData(_bloodPressureView, this);
        bodyTemperatureObservableData = new BodyTemperatureObservableData(_bodyTemperatureView, this);
        bloodGlucoseData = new BloodGlucoseObservableData(_bloodGlucoseView, this);
    }

    void connectToServer(ResourceName resource) {
        Util.toast("trying to connect to server...");

        disconnectFromServer(lastConnectedResource);

        // 중복처리
        // lastConnectedResource = null;

        try {
            switch (resource) {
                case SPO2:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_SPO2,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            this
                    );
                    break;

                case HEART_RATE:
                    OcPlatform.findResource("",
                            QUERY_HEART_RATE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            this
                    );
                    break;

                case BLOOD_PRESSURE:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_PRESSURE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            this
                    );
                    break;

                case BODY_TEMPERATURE:
                    OcPlatform.findResource("",
                            QUERY_BODY_TEMPERATURE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            this
                    );
                    break;

                case BLOOD_GLUCOSE:
                    OcPlatform.findResource("",
                            QUERY_BLOOD_GLUCOSE,
                            EnumSet.of(OcConnectivityType.CT_DEFAULT),
                            this
                    );
                    break;

            }
        } catch (OcException e) {
            Util.toast(e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    // stop observation
    void disconnectFromServer(ResourceName resource) {
        if (resource == null) {
            return;
        }

        if (lastConnectedResource == resource) {
            lastConnectedResource = null;
        }

        try {
            switch (resource) {
                case SPO2:
                    foundBloodSpO2Resource.cancelObserve();
                    break;

                case HEART_RATE:
                    foundHeartRateResource.cancelObserve();
                    break;

                case BLOOD_PRESSURE:
                    foundBloodPressureResource.cancelObserve();
                    break;

                case BODY_TEMPERATURE:
                    foundBodyTemperatureResource.cancelObserve();
                    break;

                case BLOOD_GLUCOSE:
                    foundBloodGlucoseResource.cancelObserve();
                    break;

            }
        } catch (OcException e) {
            Util.toast(e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public synchronized void onResourceFound(OcResource ocResource) {
        if (null == ocResource) {
            Util.toast("onResourceFound():ocResource is null");
            Log.e(TAG, "ocResource is null");

            return;
        }

        // Get the resource URI
        String resourceUri = ocResource.getUri();
//        Util.toast("onResourceFound(): resourceUri: " + resourceUri);
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
            case URI_BLOOD_SPO2:
                foundBloodSpO2Resource = ocResource;
                getBloodSpO2ResourceRepresentation();
                break;

            case URI_HEART_RATE:
                foundHeartRateResource = ocResource;
                getHeartBeatResourceRepresentation();
                break;

            case URI_BLOOD_PRESSURE:
                foundBloodPressureResource = ocResource;
                getBloodPressureResourceRepresentation();
                break;

            case URI_BODY_TEMPERATURE:
                foundBodyTemperatureResource = ocResource;
                getBodyTemperatureResourceRepresentation();
                break;

            case URI_BLOOD_GLUCOSE:
                foundBloodGlucoseResource = ocResource;
                getBloodGlucoseResourceRepresentation();
                break;

            default:
                break;
        }
    }

    private void getBloodGlucoseResourceRepresentation() {
//        Util.toast("Getting Blood Glucose Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodGlucoseResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodPressureResourceRepresentation() {
//        Util.toast("Getting Blood Pressure Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodPressureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBloodSpO2ResourceRepresentation() {
//        Util.toast("Getting Blood SpO2 Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBloodSpO2Resource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getBodyTemperatureResourceRepresentation() {
//        Util.toast("Getting Body Temperature Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundBodyTemperatureResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast("Error occurred while invoking \"get\" API");
        }
    }

    private void getHeartBeatResourceRepresentation() {
//        Util.toast("Getting HeartBeat Representation...");

        Map<String, String> queryParams = new HashMap<>();
        try {
            SystemClock.sleep(1);
            foundHeartRateResource.get(queryParams, this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Util.toast("Error occurred while invoking \"get\" API");
        }
    }

    @Override
    public void onGetCompleted(List<OcHeaderOption> list, final OcRepresentation ocRepresentation) {
        String resourceUri = ocRepresentation.getUri();
        Log.w(TAG, "getUri(): " + ocRepresentation.getUri());

        try {
            switch (resourceUri) {
                case URI_BLOOD_SPO2:
                    bloodSpO2Data.setData(ocRepresentation.getValue(KEY_BLOOD_SPO2));

                    foundBloodSpO2Resource.observe(
                            ObserveType.OBSERVE,
                            new HashMap<String, String>(),
                            bloodSpO2Data);
                    break;

                case URI_HEART_RATE:
                    heartRateObservableData.setData(ocRepresentation.getValue(KEY_HEART_RATE));

                    foundHeartRateResource.observe(
                            ObserveType.OBSERVE,
                            new HashMap<String, String>(),
                            heartRateObservableData);
                    break;

                case URI_BLOOD_PRESSURE:
                    bloodPressureObservableData.setData(ocRepresentation.getValue(KEY_BLOOD_PRESSURE));

                    foundBloodPressureResource.observe(
                            ObserveType.OBSERVE,
                            new HashMap<String, String>(),
                            bloodPressureObservableData);
                    break;

                case URI_BODY_TEMPERATURE:
                    bodyTemperatureObservableData.setData(ocRepresentation.getValue(KEY_BODY_TEMPERATURE));
                    SystemClock.sleep(1);

                    foundBodyTemperatureResource.observe(
                            ObserveType.OBSERVE,
                            new HashMap<String, String>(),
                            bodyTemperatureObservableData);
                    break;

                case URI_BLOOD_GLUCOSE:
                    bloodGlucoseData.setData(ocRepresentation.getValue(KEY_BLOOD_GLUCOSE));
                    SystemClock.sleep(1);

                    foundBloodGlucoseResource.observe(
                            ObserveType.OBSERVE,
                            new HashMap<String, String>(),
                            bloodGlucoseData);
                    break;

                default:
                    break;
            }
        } catch (OcException e) {
            e.printStackTrace();
        }

        update(null, null);
    }

    @Override
    public synchronized void onGetFailed(Throwable throwable) {
        Util.toast(throwable.getMessage());
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void update(Observable observable, Object data) {
        updateDate.post(updateDateRunner);
    }
}
