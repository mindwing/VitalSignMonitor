package kr.re.etri.iotivity.vitalsignserver;

import org.iotivity.base.OcPlatform;

/**
 * Created by mindwing on 2016-01-25.
 *
 * OIC_Healthcare_Resource_Specification_v0.2.1
 */
public interface HealthCareResourceSpec {
    String RESOURCE_TYPE_BLOOD_GLUCOSE = "oic.r.sensor.health.blood.glucose";
    String RESOURCE_TYPE_BLOOD_PRESSURE = "oic.r.sensor.health.blood.pressure";
    String RESOURCE_TYPE_BLOOD_SPO2 = "oic.r.health.blood.o2.saturation";
    String RESOURCE_TYPE_BODY_TEMPERATURE = "oic.r.health.bodytemperature";
    String RESOURCE_TYPE_HEART_RATE = "oic.r.sensor.heartrate";

    String QUERY_BLOOD_GLUCOSE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_GLUCOSE;
    String QUERY_BLOOD_PRESSURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_PRESSURE;
    String QUERY_BLOOD_SPO2 = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_SPO2;
    String QUERY_BODY_TEMPERATURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BODY_TEMPERATURE;
    String QUERY_HEART_RATE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_HEART_RATE;

    String KEY_NAME = "name";
    String KEY_BLOOD_GLUCOSE = "bloodsugar";
    String KEY_BLOOD_PRESSURE = "bloodPressure";
    String KEY_BLOOD_SPO2 = "spo2";
    String KEY_BODY_TEMPERATURE = "temperature";
    String KEY_HEART_RATE = "heartRate";

    String URI_BLOOD_GLUCOSE = "/a/blood_glucose";
    String URI_BLOOD_PRESSURE = "/a/blood_pressure";
    String URI_BLOOD_SPO2 = "/a/blood_spo2";
    String URI_BODY_TEMPERATURE = "/a/body_temperature";
    String URI_HEART_RATE = "/a/heart_rate";
}
