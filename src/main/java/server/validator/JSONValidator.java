package server.validator;

import model.Device;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Server;
import server.exception.ProtocolException;

import java.time.Instant;

public class JSONValidator implements Validator {

    private final JSONObject json;

    private JSONValidator(JSONObject json) {
        this.json = json;
    }

    public static JSONValidator getInstance(String message) throws ProtocolException {
        JSONParser jsonParser = new JSONParser();
        try {
            return new JSONValidator((JSONObject) jsonParser.parse(message));
        } catch (ParseException e) {
            throw new ProtocolException(ProtocolException.COMMAND_FORMAT_NOT_VALIDATED);
        }
    }

    public String getCommand() throws ProtocolException {
        String command = (String) json.get(Server.COMMAND);
        if (command == null)
            throw new ProtocolException(ProtocolException.COMMAND_PROPERTY_NOT_VALIDATED);
        return command;
    }

    public int getDeviceSerial() throws ProtocolException {
        String deviceSerialString = String.valueOf(json.get(Server.DEVICE_SERIAL));
        if (deviceSerialString == null)
            throw new ProtocolException(ProtocolException.DEVICE_SERIAL_PROPERTY_NOT_VALIDATED);
        try {
            return Integer.parseInt(deviceSerialString);
        } catch (NumberFormatException e) {
            throw new ProtocolException(ProtocolException.DEVICE_SERIAL_PROPERTY_NOT_VALIDATED);
        }
    }

    @Override
    public String getPrivateKey() throws ProtocolException {
        String privateKey = (String) json.get(Server.PRIVATE_KEY);
        if (privateKey == null || Device.KEY_MIN_LEN <= privateKey.length() && privateKey.length() <= Device.KEY_MAX_LEN)
            throw new ProtocolException(ProtocolException.PRIVATE_KEY_PROPERTY_NOT_VALIDATED);
        return privateKey;
    }

    public String getPublicKey() throws ProtocolException {
        String publicKey = (String) json.get(Server.PUBLIC_KEY);
        if (publicKey == null || !(Device.KEY_MIN_LEN <= publicKey.length() && publicKey.length() <= Device.KEY_MAX_LEN))
            throw new ProtocolException(ProtocolException.PUBLIC_KEY_PROPERTY_NOT_VALIDATED);
        return publicKey;
    }

    @Override
    public Instant getTimeInstant() {
        // TODO
        return null;
    }

    @Override
    public String getDataKey() throws ProtocolException {
        throw new ProtocolException(ProtocolException.NOT_IMPLEMENTED);
    }

    @Override
    public double getDataValue() throws ProtocolException {
        throw new ProtocolException(ProtocolException.NOT_IMPLEMENTED);
    }
}
