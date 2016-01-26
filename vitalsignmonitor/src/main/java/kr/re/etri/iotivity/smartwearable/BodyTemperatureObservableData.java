package kr.re.etri.iotivity.smartwearable;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import java.util.Observer;

/**
 * 체온에 대한 Observer 클래스
 * <br>
 * Property name: temperature
 * Value type: number
 */
public class BodyTemperatureObservableData<Integer> extends ObservableData {

    public BodyTemperatureObservableData(TextView[] _views, Observer _observer) {
        super(_views, _observer);
    }

    @Override
    public void parseData(OcRepresentation ocRepresentation) throws OcException {
        try {
            if (ocRepresentation.hasAttribute(KEY_BODY_TEMPERATURE)) {
                data = ocRepresentation.getValue(KEY_BODY_TEMPERATURE);

                notifyObserver();
            }
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }
}

