package server.validator;

import server.exception.ProtocolException;

import java.time.Instant;

public interface Validator {
    int getDeviceSerial() throws ProtocolException;
    String getPrivateKey() throws ProtocolException;
    String getPublicKey() throws ProtocolException;
    Instant getTimeInstant() throws ProtocolException;
    String getDataKey() throws ProtocolException;
    double getDataValue() throws ProtocolException;
}
