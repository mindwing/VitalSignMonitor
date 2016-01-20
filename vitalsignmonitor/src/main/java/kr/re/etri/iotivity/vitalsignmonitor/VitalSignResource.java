package kr.re.etri.iotivity.vitalsignmonitor;

import org.iotivity.base.OcPlatform;

/**
 * Created by mindwing on 2016-01-20.
 */
public class VitalSignResource {
    static final String RESOURCE_TYPE_BLOOD_GLUCOSE = "oic.r.sensor.health.blood.glucose";
    static final String RESOURCE_TYPE_BLOOD_PRESSURE = "oic.r.sensor.health.blood.pressure";
    static final String RESOURCE_TYPE_BLOOD_SPO2 = "oic.r.health.blood.o2.saturation";
    static final String RESOURCE_TYPE_BODY_TEMPERATURE = "oic.r.health.bodytemperature";
    static final String RESOURCE_TYPE_HEART_RATE = "oic.r.sensor.heartrate";

    static final String QUERY_BLOOD_GLUCOSE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_GLUCOSE;
    static final String QUERY_BLOOD_PRESSURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_PRESSURE;
    static final String QUERY_BLOOD_SPO2 = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BLOOD_SPO2;
    static final String QUERY_BODY_TEMPERATURE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_BODY_TEMPERATURE;
    static final String QUERY_HEART_RATE = OcPlatform.WELL_KNOWN_QUERY + "?rt=" + RESOURCE_TYPE_HEART_RATE;

    static final String KEY_NAME = "name";
    static final String KEY_BLOOD_GLUCOSE = "blood_glucose";
    static final String KEY_BLOOD_PRESSURE_SYSTOLIC = "pressure_systolic";
    static final String KEY_BLOOD_PRESSURE_DIASTOLIC = "pressure_diastolic";
    static final String KEY_BLOOD_SPO2 = "spo2";
    static final String KEY_BODY_TEMPERATURE = "body_temperature";
    static final String KEY_HEART_RATE = "heart_rate";

    static final String URI_BLOOD_GLUCOSE = "/a/blood_glucose";
    static final String URI_BLOOD_PRESSURE = "/a/blood_pressure";
    static final String URI_BLOOD_SPO2 = "/a/blood_spo2";
    static final String URI_BODY_TEMPERATURE = "/a/body_temperature";
    static final String URI_HEART_RATE = "/a/heart_rate";
}
