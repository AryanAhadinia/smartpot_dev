package model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import db.timeseries.InfluxManager;

import java.time.Instant;

@Measurement(name = "SensorData")
public record SensorData(@Column(timestamp = true) Instant time, @Column(tag = true) int deviceSerial,
                         @Column(tag = true) String sensorName, @Column double value) {

    public void save() {
        InfluxManager.insertData(this);
    }
}
