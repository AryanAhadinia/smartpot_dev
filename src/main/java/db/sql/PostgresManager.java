package db.sql;

import model.Device;

import java.sql.*;

public class PostgresManager {
    public static final String DEVICE_TABLE = "device";
    public static final String DEVICE_SERIAL = "deviceSerial";
    public static final String DEVICE_PUBLIC_KEY = "privateKey";
    public static final String DEVICE_PRIVATE_KEY = "publicKey";

    private static Connection getConnection() {
        return PostgresConnection.getInstance().getConnection();
    }

    public static void createTables() throws SQLException {
        createDeviceTable();
    }

    private static void createDeviceTable() throws SQLException {
        String sql = "create table if not exists " + DEVICE_TABLE + " (" +
                DEVICE_SERIAL + " int primary key, " +
                DEVICE_PUBLIC_KEY + " varchar not null, " +
                DEVICE_PRIVATE_KEY + " varchar not null);";
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
        }
    }

    public static Device getDeviceBySerial(int serial) throws SQLException {
        String sql = "select * from " + DEVICE_TABLE + " where " + DEVICE_SERIAL + " = ?;";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, serial);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return new Device(serial,
                            resultSet.getString(DEVICE_PUBLIC_KEY),
                            resultSet.getString(DEVICE_PRIVATE_KEY));
                return null;
            }
        }
    }

    public static void updateDevicePublicKey(int serial, String publicKey) throws SQLException {
        String sql = "update " + DEVICE_TABLE + " " +
                "set " + DEVICE_PUBLIC_KEY + " = ? " +
                "where " + DEVICE_SERIAL + " = ?;";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, serial);
            statement.setString(2, publicKey);
            statement.executeUpdate();
        }
    }
}
