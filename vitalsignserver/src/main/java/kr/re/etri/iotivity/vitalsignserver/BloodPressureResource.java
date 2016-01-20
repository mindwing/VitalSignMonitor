package kr.re.etri.iotivity.vitalsignserver;

import android.util.Log;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

/**
 * Created by mindwing on 2016-01-20.
 */
public class BloodPressureResource extends HealthCareResource {

    private int systolicPressure = 120;
    private int diastolicPressure = 80;

    public BloodPressureResource(String _name) {
        name = _name;

        resourceUri = URI_BLOOD_PRESSURE;
        resourceType = RESOURCE_TYPE_BLOOD_PRESSURE;
    }

    @Override
    public void setOcRepresentation(OcRepresentation rep) {
        try {
            if (rep.hasAttribute(KEY_NAME)) name = rep.getValue(KEY_NAME);
            if (rep.hasAttribute(KEY_BLOOD_PRESSURE_SYSTOLIC))
                systolicPressure = rep.getValue(KEY_BLOOD_PRESSURE_SYSTOLIC);
            if (rep.hasAttribute(KEY_BLOOD_PRESSURE_DIASTOLIC))
                diastolicPressure = rep.getValue(KEY_BLOOD_PRESSURE_DIASTOLIC);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to get representation values");
        }
    }

    @Override
    public OcRepresentation getCurrentRepresentation() {
        OcRepresentation rep = new OcRepresentation();
        try {
            rep.setValue(KEY_NAME, name);
            rep.setValue(KEY_BLOOD_PRESSURE_SYSTOLIC, systolicPressure);
            rep.setValue(KEY_BLOOD_PRESSURE_DIASTOLIC, diastolicPressure);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to set representation values");
        }

        return rep;
    }
}
