package kr.re.etri.iotivity.vitalsignserver;

import android.util.Log;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import kr.re.etri.iotivity.smartwearable.HealthCareResource;

/**
 * Created by mindwing on 2016-01-20.
 *
 * Property name: heartRate
 * Value type: number
 */
public class HeartRateResource extends HealthCareResource {

    private int heartRate = 60;

    public HeartRateResource(String _name) {
        name = _name;

        resourceUri = URI_HEART_RATE;
        resourceType = RESOURCE_TYPE_HEART_RATE;
    }

    @Override
    public void changeValue() {
        heartRate += getRandomNumber();
    }

    @Override
    public void setOcRepresentation(OcRepresentation rep) {
        try {
            if (rep.hasAttribute(KEY_NAME)) name = rep.getValue(KEY_NAME);
            if (rep.hasAttribute(KEY_HEART_RATE))
                heartRate = rep.getValue(KEY_HEART_RATE);
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
            rep.setValue(KEY_HEART_RATE, heartRate);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to set representation values");
        }

        return rep;
    }
}
