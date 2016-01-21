package kr.re.etri.iotivity.vitalsignmonitor;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;

import java.util.List;

/**
 * Created by mindwing on 2016-01-20.
 */
public abstract class ObservableData implements OcResource.OnObserveListener, Runnable {
    String TAG;

    TextView view;

    int data;

    public ObservableData(TextView _view) {
        view = _view;
        TAG = getClass().getSimpleName();
    }

    public abstract void parseData(OcRepresentation ocRepresentation) throws OcException;

    @Override
    public synchronized void onObserveCompleted(List<OcHeaderOption> headerOptionList, OcRepresentation ocRepresentation, int sequenceNumber) {
        if (OcResource.OnObserveListener.REGISTER == sequenceNumber) {
            Log.d(TAG, "Observe registration action is successful:");
        } else if (OcResource.OnObserveListener.DEREGISTER == sequenceNumber) {
            Log.d(TAG, "Observe De-registration action is successful");
        } else if (OcResource.OnObserveListener.NO_OPTION == sequenceNumber) {
            Log.d(TAG, "Observe registration or de-registration action is failed");
        }

        Log.d(TAG, "OBSERVE Result:\tSequenceNumber: " + sequenceNumber);

        try {
            parseData(ocRepresentation);

            Log.w(TAG, "parseData() - " + data);
            view.post(this);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get the attribute values");
        }

        //        mFoundLightResource.cancelObserve();
    }

    @Override
    public synchronized void onObserveFailed(Throwable ex) {

    }

    @Override
    public void run() {
        view.setText(Integer.toString(data));
        view.invalidate();
    }
}
