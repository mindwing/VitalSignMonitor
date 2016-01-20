package kr.re.etri.iotivity.vitalsignserver;

import android.util.Log;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

/**
 * Created by mindwing on 2016-01-20.
 */
public class BodyTemperatureResource extends HealthCareResource {

    private int bodyTemperature = 365;

    public BodyTemperatureResource(String _name) {
        name = _name;

        resourceUri = URI_BODY_TEMPERATURE;
        resourceType = RESOURCE_TYPE_BODY_TEMPERATURE;
    }

    @Override
    public void setOcRepresentation(OcRepresentation rep) {
        try {
            if (rep.hasAttribute(KEY_NAME)) name = rep.getValue(KEY_NAME);
            if (rep.hasAttribute(KEY_BODY_TEMPERATURE))
                bodyTemperature = rep.getValue(KEY_BODY_TEMPERATURE);
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
            rep.setValue(KEY_BODY_TEMPERATURE, bodyTemperature);
        } catch (OcException e) {
            Log.e(TAG, e.toString());
            Log.d(TAG, "Failed to set representation values");
        }

        return rep;
    }
}
