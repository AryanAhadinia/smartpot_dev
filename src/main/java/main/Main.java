package main;

import db.sql.PostgresConnection;
import db.sql.PostgresManager;
import db.timeseries.InfluxClient;
import server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    private static final String DB_PROPERTIES = "db.properties";

    public static void main(String[] args) {
        System.out.println("Server initialized.");
        try (InputStream propertiesStream = ClassLoader.getSystemResourceAsStream(DB_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(propertiesStream);

            String postgresURL = properties.getProperty("PSQL_URL");
            String postgresUsername = properties.getProperty("PSQL_USER");
            String postgresPassword = properties.getProperty("PSQL_PASS");
            String influxURL = properties.getProperty("INFLUX_URL");
            String influxToken = properties.getProperty("INFLUX_TOKEN");

//            PostgresConnection.createInstance(postgresURL, postgresUsername, postgresPassword);
//            InfluxClient.createInstance(influxURL, influxToken);
//            PostgresManager.createTables();

            Server server = Server.getInstance(8080);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
        }
    }
}
