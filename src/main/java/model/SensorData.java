package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import db.timeseries.InfluxManager;

import java.time.Instant;

@Measurement(name = "SensorData")
public final class SensorData {
    @Column(timestamp = true)
    private final Instant time;
    @Column(tag = true)
    private final int deviceSerial;
    @Column(tag = true)
    private final String sensorName;
    @Column
    private final double value;

    public SensorData(Instant time, int deviceSerial, String sensorName, double value) {
        this.time = time;
        this.deviceSerial = deviceSerial;
        this.sensorName = sensorName;
        this.value = value;
    }

    public void save() {
        InfluxManager.insertData(this);
    }

    public double getValue() {
        return value;
    }
}
