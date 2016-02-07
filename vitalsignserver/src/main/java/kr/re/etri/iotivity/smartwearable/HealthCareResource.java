package kr.re.etri.iotivity.smartwearable;

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
import java.util.Map;
import java.util.Random;

/**
 * Created by mindwing on 2016-01-20.
 */
public abstract class HealthCareResource implements OcPlatform.EntityHandler, HealthCareResourceSpec {
    private final static int SUCCESS = 200;
    public static String TAG;
    private static Random random = new Random();
    public String resourceUri;                 // resource URI
    public String resourceType;                // resource type
    public String name;                        // resource name
    OcResourceHandle resourceHandle;    // resource handle
    String resourceInterface = OcPlatform.DEFAULT_INTERFACE;          // resource interface.
    private Thread notifyThread;

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
                break;
        }
        return ehResult;
    }

    private EntityHandlerResult handleGetRequest(final OcResourceRequest request) {
        EntityHandlerResult ehResult;
        OcResourceResponse response = new OcResourceResponse();
        response.setRequestHandle(request.getRequestHandle());
        response.setResourceHandle(request.getResourceHandle());

        response.setErrorCode(SUCCESS);
        response.setResponseResult(EntityHandlerResult.OK);
        response.setResourceRepresentation(getCurrentRepresentation());
        ehResult = sendResponse(response);

        return ehResult;
    }

    private synchronized EntityHandlerResult handleObserver(final OcResourceRequest request) {
        ObservationInfo observationInfo = request.getObservationInfo();
        switch (observationInfo.getObserveAction()) {
            case REGISTER:
//                mObservationId = observationInfo.getOcObservationId();

                if (notifyThread != null) {
//                    notifyThread.interrupt();
                    break;
                }

                (notifyThread = new Thread() {
                    public void run() {
                        notifyObservers(request);
                    }
                }).start();
                break;

            case UNREGISTER:
//                mObservationId = 0;
                break;
        }

        return EntityHandlerResult.OK;
    }

    abstract public void changeValue();

    private void notifyObservers(OcResourceRequest request) {
        while (true) {
            SystemClock.sleep(1000);

            Log.d(TAG, "Notifying observers...");
            Log.d(TAG, toString());

            try {
                changeValue();
                OcPlatform.notifyAllObservers(resourceHandle);
            } catch (OcException e) {
                ErrorCode errorCode = e.getErrorCode();
                if (ErrorCode.NO_OBSERVERS == errorCode) {
                    Log.d(TAG, "No more observers, stopping notifications");
                }
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

    public int getRandomNumber() {
        return random.nextInt(7) - 3;
    }

    @Override
    public String toString() {
        return "\t" + "URI" + ": " + resourceUri +
                "\n\t" + KEY_NAME + ": " + name;
    }
}
