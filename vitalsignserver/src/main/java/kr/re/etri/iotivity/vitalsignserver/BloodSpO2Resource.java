package kr.re.etri.iotivity.vitalsignserver;

import android.util.Log;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import kr.re.etri.iotivity.smartwearable.HealthCareResource;

/**
 * Created by mindwing on 2016-01-20.
 *
 * Property name: spo2
 * Value type: number
 */
public class BloodSpO2Resource extends HealthCareResource {

    private int spO2 = 99;

    public BloodSpO2Resource(String _name) {
        name = _name;

        resourceUri = URI_BLOOD_SPO2;
        resourceType = RESOURCE_TYPE_BLOOD_SPO2;
    }

    @Override
    public void changeValue() {
        spO2 += getRandomNumber();
    }

    @Override
    public void setOcRepresentation(OcRepresentation rep) {
        try {
            if (rep.hasAttribute(KEY_NAME)) name = rep.getValue(KEY_NAME);
            if (rep.hasAttribute(KEY_BLOOD_SPO2))
                spO2 = rep.getValue(KEY_BLOOD_SPO2);
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
            rep.setValue(KEY_BLOOD_SPO2, spO2);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to set representation values");
        }

        return rep;
    }
}
