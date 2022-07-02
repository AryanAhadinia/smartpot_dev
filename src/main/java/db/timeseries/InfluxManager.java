package db.timeseries;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import model.SensorData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InfluxManager {
    private final static String BUCKET = "Sensor";
    private final static String ORG = "SmartPotServer";

    private static InfluxDBClient getClient() {
        return InfluxClient.getInstance().getClient();
    }

    public static void insertData(SensorData sensorData) {
        WriteApiBlocking writeApi = getClient().getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.S, sensorData);
    }

    public static ArrayList<SensorData> getData(int deviceSerial, Instant startTime) {
        String flux = "SELECT * FROM " + BUCKET;
        List<FluxTable> tables = getClient().getQueryApi().query(flux, ORG);
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                System.out.println(record);
            }
        }
        return null;
    }
}
