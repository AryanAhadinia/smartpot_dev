package model;

import db.sql.PostgresManager;

import java.sql.SQLException;

public final class Device {
    public static final int KEY_MIN_LEN = 32;
    public static final int KEY_MAX_LEN = 32;
    private final int deviceSerial;
    private final String privateKey;
    private String publicKey;

    public Device(int deviceSerial, String privateKey, String publicKey) {
        this.deviceSerial = deviceSerial;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public int getDeviceSerial() {
        return deviceSerial;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) throws SQLException {
        PostgresManager.updateDevicePublicKey(deviceSerial, publicKey);
        this.publicKey = publicKey;
    }

    public static Device getDeviceBySerial(int deviceSerial) throws SQLException {
        return PostgresManager.getDeviceBySerial(deviceSerial);
    }
}
