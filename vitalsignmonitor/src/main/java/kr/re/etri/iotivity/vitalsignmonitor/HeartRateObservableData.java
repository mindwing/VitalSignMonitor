package kr.re.etri.iotivity.vitalsignmonitor;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

/**
 * Created by mindwing on 2016-01-20.
 *
 * Property name: heartRate
 * Value type: number
 */
public class HeartRateObservableData<Integer> extends ObservableData {

    public HeartRateObservableData(TextView _view) {
        super(_view);
    }

    @Override
    public void parseData(OcRepresentation ocRepresentation) throws OcException {
        try {
            if (ocRepresentation.hasAttribute(KEY_HEART_RATE))
                data = ocRepresentation.getValue(KEY_HEART_RATE);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }

    @Override
    public synchronized void onObserveFailed(Throwable ex) {

    }
}

