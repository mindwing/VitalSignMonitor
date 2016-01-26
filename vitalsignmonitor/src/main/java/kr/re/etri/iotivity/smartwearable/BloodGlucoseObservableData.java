package kr.re.etri.iotivity.smartwearable;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import java.util.Observer;

/**
 * 혈당에 대한 Observer 클래스
 * <br>
 * Property name: bloodsugar
 * Value type: number
 */
public class BloodGlucoseObservableData<Integer> extends ObservableData {

    public BloodGlucoseObservableData(TextView[] _views, Observer _observer) {
        super(_views, _observer);
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

