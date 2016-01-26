package kr.re.etri.iotivity.smartwearable;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import java.util.Observer;

/**
 * 혈중 산소포화도에 대한 Observer 클래스
 * <br>
 * Property name: spo2
 * Value type: number
 */
public class BloodSpO2ObservableData<Integer> extends ObservableData {

    public BloodSpO2ObservableData(TextView[] _views, Observer _observer) {
        super(_views, _observer);
    }

    @Override
    public void parseData(OcRepresentation ocRepresentation) throws OcException {
        try {
            if (ocRepresentation.hasAttribute(KEY_BLOOD_SPO2)) {
                data = ocRepresentation.getValue(KEY_BLOOD_SPO2);

                notifyObserver();
            }
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }
}

