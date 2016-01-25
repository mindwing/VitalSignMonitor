package kr.re.etri.iotivity.vitalsignmonitor;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import java.util.Observer;

/**
 * Created by mindwing on 2016-01-20.
 *
 * Property name: bloodsugar
 * Value type: number
 */
public class BloodGlucoseObservableData<Integer> extends ObservableData {

    public BloodGlucoseObservableData(TextView _view, Observer _observer) {
        super(_view, _observer);
    }

    @Override
    public void parseData(OcRepresentation ocRepresentation) throws OcException {
        try {
            if (ocRepresentation.hasAttribute(KEY_BLOOD_GLUCOSE)) {
                data = ocRepresentation.getValue(KEY_BLOOD_GLUCOSE);

                notifyObserver();
            }
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }

    @Override
    public synchronized void onObserveFailed(Throwable ex) {

    }
}

