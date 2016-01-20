package kr.re.etri.iotivity.vitalsignmonitor;

import android.util.Log;
import android.widget.TextView;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

/**
 * Created by mindwing on 2016-01-20.
 */
public class BloodGlucoseData extends ObservableData {

    public BloodGlucoseData(TextView _view) {
        super(_view);
    }

    @Override
    public void parseData(OcRepresentation ocRepresentation) throws OcException {
        try {
            if (ocRepresentation.hasAttribute(VitalSignResource.KEY_BLOOD_GLUCOSE))
                data = ocRepresentation.getValue(VitalSignResource.KEY_BLOOD_GLUCOSE);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }

    @Override
    public synchronized void onObserveFailed(Throwable ex) {

    }
}

