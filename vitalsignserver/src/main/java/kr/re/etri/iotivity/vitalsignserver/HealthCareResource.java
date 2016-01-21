package kr.re.etri.iotivity.vitalsignserver;

import android.os.SystemClock;
import android.util.Log;

import org.iotivity.base.EntityHandlerResult;
import org.iotivity.base.ErrorCode;
import org.iotivity.base.ObservationInfo;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResourceHandle;
import org.iotivity.base.OcResourceRequest;
import org.iotivity.base.OcResourceResponse;
import org.iotivity.base.RequestHandlerFlag;
import org.iotivity.base.RequestType;
import org.iotivity.base.ResourceProperty;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mindwing on 2016-01-20.
 */
public abstract class HealthCareResource implements OcPlatform.EntityHandler {
    static String TAG;

    static final String RESOURCE_TYPE_BLOOD_GLUCOSE = "oic.r.sensor.health.blood.glucose";
    static final String RESOURCE_TYPE_BLOOD_PRESSURE = "oic.r.sensor.health.blood.pressure";
    static final String RESOURCE_TYPE_BLOOD_SPO2 = "oic.r.health.blood.o2.saturation";
    static final String RESOURCE_TYPE_BODY_TEMPERATURE = "oic.r.health.bodytemperature";
    static final String RESOURCE_TYPE_HEART_RATE = "oic.r.sensor.heartrate";

    static final String QUERY_BLOOD_GLUCOSE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_GLUCOSE;
    static final String QUERY_BLOOD_PRESSURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_PRESSURE;
    static final String QUERY_BLOOD_SPO2 = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_SPO2;
    static final String QUERY_BODY_TEMPERATURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BODY_TEMPERATURE;
    static final String QUERY_HEART_RATE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_HEART_RATE;

    static final String KEY_NAME = "name";
    static final String KEY_BLOOD_GLUCOSE = "bloodsugar";
    // @TODO bloodPressure (dia, sys) 로 변경해야 함
    static final String KEY_BLOOD_PRESSURE_SYSTOLIC = "pressure_systolic";
    static final String KEY_BLOOD_PRESSURE_DIASTOLIC = "pressure_diastolic";
    static final String KEY_BLOOD_SPO2 = "spo2";
    static final String KEY_BODY_TEMPERATURE = "temperature";
    static final String KEY_HEART_RATE = "heartRate";

    static final String URI_BLOOD_GLUCOSE = "/a/blood_glucose";
    static final String URI_BLOOD_PRESSURE = "/a/blood_pressure";
    static final String URI_BLOOD_SPO2 = "/a/blood_spo2";
    static final String URI_BODY_TEMPERATURE = "/a/body_temperature";
    static final String URI_HEART_RATE = "/a/heart_rate";

    OcResourceHandle resourceHandle;    // resource handle
    String resourceUri;                 // resource URI
    String resourceType;                // resource type
    String resourceInterface = OcPlatform.DEFAULT_INTERFACE;          // resource interface.
    String name;                        // resource name

    private final static int SUCCESS = 200;
    //    private boolean mIsSlowResponse = false;
    boolean isListOfObservers = false;
    Thread observerNotifier;

    public HealthCareResource() {
        TAG = getClass().getSimpleName();
    }

    public abstract void setOcRepresentation(OcRepresentation rep);

    public abstract OcRepresentation getCurrentRepresentation();

    public synchronized void registerResource() throws OcException {
        if (resourceHandle == null) {
            resourceHandle = OcPlatform.registerResource(
                    resourceUri,
                    resourceType,
                    resourceInterface,
                    this,
                    EnumSet.of(ResourceProperty.DISCOVERABLE, ResourceProperty.OBSERVABLE)
            );
        }
    }

    public synchronized EntityHandlerResult handleEntity(final OcResourceRequest request) {
        EntityHandlerResult ehResult = EntityHandlerResult.ERROR;
        if (request == null) {
            Log.d(TAG, "Server request is invalid");
            return ehResult;
        }

        EnumSet<RequestHandlerFlag> requestFlags = request.getRequestHandlerFlagSet();
        if (requestFlags.contains(RequestHandlerFlag.INIT)) {
            Log.d(TAG, "\t\tRequest Flag: Init");
            ehResult = EntityHandlerResult.OK;
        }

        if (requestFlags.contains(RequestHandlerFlag.REQUEST)) {
            Log.d(TAG, "\t\tRequest Flag: Request");
            ehResult = handleRequest(request);
        }

        if (requestFlags.contains(RequestHandlerFlag.OBSERVER)) {
            Log.d(TAG, "\t\tRequest Flag: Observer");
            ehResult = handleObserver(request);
        }

        return ehResult;
    }


    private EntityHandlerResult handleRequest(OcResourceRequest request) {
        EntityHandlerResult ehResult = EntityHandlerResult.ERROR;
        // Check for query params (if any)
        Map<String, String> queries = request.getQueryParameters();
        if (!queries.isEmpty()) {
            Log.d(TAG, "Query processing is up to entityHandler");
        } else {
            Log.d(TAG, "No query parameters in this request");
        }

        for (Map.Entry<String, String> entry : queries.entrySet()) {
            Log.d(TAG, "Query key: " + entry.getKey() + " value: " + entry.getValue());
        }

        //Get the request type
        RequestType requestType = request.getRequestType();
        switch (requestType) {
            case GET:
                Log.d(TAG, "\t\t\tRequest Type is GET");
                ehResult = handleGetRequest(request);
                break;
            case PUT:
                Log.d(TAG, "\t\t\tRequest Type is PUT");
//                ehResult = handlePutRequest(request);
                break;
            case POST:
                Log.d(TAG, "\t\t\tRequest Type is POST");
//                ehResult = handlePostRequest(request);
                break;
            case DELETE:
                Log.d(TAG, "\t\t\tRequest Type is DELETE");
//                ehResult = handleDeleteRequest();
                break;
        }
        return ehResult;
    }

    private EntityHandlerResult handleGetRequest(final OcResourceRequest request) {
        EntityHandlerResult ehResult;
        OcResourceResponse response = new OcResourceResponse();
        response.setRequestHandle(request.getRequestHandle());
        response.setResourceHandle(request.getResourceHandle());

//        if (mIsSlowResponse) { // Slow response case
//            new Thread(new Runnable() {
//                public void run() {
//                    handleSlowResponse(request);
//                }
//            }).start();
//            ehResult = EntityHandlerResult.SLOW;
//        } else { // normal response case.
        response.setErrorCode(SUCCESS);
        response.setResponseResult(EntityHandlerResult.OK);
        response.setResourceRepresentation(getCurrentRepresentation());
        ehResult = sendResponse(response);
//        }
        return ehResult;
    }

//    private void handleSlowResponse(OcResourceRequest request) {
//        SystemClock.sleep(10);
//
//        Log.d(TAG, "Sending slow response...");
//        OcResourceResponse response = new OcResourceResponse();
//        response.setRequestHandle(request.getRequestHandle());
//        response.setResourceHandle(request.getResourceHandle());
//
//        response.setErrorCode(SUCCESS);
//        response.setResponseResult(EntityHandlerResult.OK);
//        response.setResourceRepresentation(getOcRepresentation());
//        sendResponse(response);
//    }

    private List<Byte> mObservationIds; //IDs of observes

    private EntityHandlerResult handleObserver(final OcResourceRequest request) {
        ObservationInfo observationInfo = request.getObservationInfo();
        switch (observationInfo.getObserveAction()) {
            case REGISTER:
                if (null == mObservationIds) {
                    mObservationIds = new LinkedList<>();
                }
                mObservationIds.add(observationInfo.getOcObservationId());
                break;
            case UNREGISTER:
                mObservationIds.remove((Byte) observationInfo.getOcObservationId());
                break;
        }
        // Observation happens on a different thread in notifyObservers method.
        // If we have not created the thread already, we will create one here.
        if (null == observerNotifier) {
            observerNotifier = new Thread(new Runnable() {
                public void run() {
                    notifyObservers(request);
                }
            });
            observerNotifier.start();
        }
        return EntityHandlerResult.OK;
    }

    public void increase() {}

    private void notifyObservers(OcResourceRequest request) {
        while (true) {
            SystemClock.sleep(1000);

            Log.d(TAG, "Notifying observers...");
            Log.d(TAG, toString());

            try {
//                if (isListOfObservers) {
//                    OcResourceResponse response = new OcResourceResponse();
//                    response.setErrorCode(SUCCESS);
//                    response.setResourceRepresentation(getOcRepresentation());
//                    OcPlatform.notifyListOfObservers(
//                            bloodPressureResourceHandle,
//                            mObservationIds,
//                            response);
//                } else {
                increase();
                OcPlatform.notifyAllObservers(resourceHandle);
//                }
            } catch (OcException e) {
                ErrorCode errorCode = e.getErrorCode();
                if (ErrorCode.NO_OBSERVERS == errorCode) {
                    Log.d(TAG, "No more observers, stopping notifications");
                }
                return;
            }
        }
    }

    private EntityHandlerResult sendResponse(OcResourceResponse response) {
        try {
            OcPlatform.sendResponse(response);
            return EntityHandlerResult.OK;
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to send response");
            return EntityHandlerResult.ERROR;
        }
    }

    public synchronized void unregisterResource() throws OcException {
        if (null != resourceHandle) {
            OcPlatform.unregisterResource(resourceHandle);
        }
    }

//    public void setSlowResponse(boolean isSlowResponse) {
//        mIsSlowResponse = isSlowResponse;
//    }

//    public void useListOfObservers(boolean isListOfObservers) {
//        isListOfObservers = isListOfObservers;
//    }

//    public void setContext(Context context) {
//        mContext = context;
//    }

    @Override
    public String toString() {
        return "\t" + "URI" + ": " + resourceUri +
                "\n\t" + KEY_NAME + ": " + name;
    }

//    private void msg(String text) {
//        if (null != mContext) {
//            Intent intent = new Intent("org.iotivity.base.examples.simpleserver");
//            intent.putExtra("message", text);
//            mContext.sendBroadcast(intent);
//        }
//    }
}
