package server;

import com.google.gson.Gson;
import db.timeseries.InfluxManager;
import io.javalin.Javalin;
import io.javalin.core.validation.JavalinValidation;
import model.Device;
import model.SensorData;
import server.exception.ProtocolException;
import server.live.LiveServer;
import server.validator.FormValidator;
import server.validator.JSONValidator;
import server.validator.Validator;
import server.ws.WsMessageProtocolHandler;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class Server {
    public static final String DEVICE_SERIAL = "deviceSerial";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String TIME_INSTANT = "timeInstant";
    public static final String DATA_KEY = "dataKey";
    public static final String DATA_VALUE = "dataValue";
    public static final String COMMAND = "command";

    private final int port;
    private final LiveServer liveServer;

    private Server(int port) {
        this.port = port;
        this.liveServer = new LiveServer();
    }

    public static Server getInstance(int port) {
        return new Server(port);
    }

    private Device getDeviceWithPrivateKey(Validator validator) throws ProtocolException {
        try {
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            if (!device.getPrivateKey().equals(validator.getPrivateKey())) {
                throw new ProtocolException(ProtocolException.PRIVATE_KEY_MISMATCH);
            }
            return device;
        } catch (SQLException e) {
            throw new ProtocolException(ProtocolException.INTERNAL_DATABASE_ERROR);
        }
    }

    private Device getDeviceWithPublicKey(Validator validator) throws ProtocolException {
        try {
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            if (!device.getPublicKey().equals(validator.getPublicKey())) {
                throw new ProtocolException(ProtocolException.PUBLIC_KEY_MISMATCH);
            }
            return device;
        } catch (SQLException e) {
            throw new ProtocolException(ProtocolException.INTERNAL_DATABASE_ERROR);
        }
    }

    private SensorData getSensorData(Validator validator) throws ProtocolException {
        return new SensorData(validator.getTimeInstant(), validator.getDeviceSerial(),
                validator.getDataKey(), validator.getDataValue());
    }

    public void start() {
        Javalin app = Javalin.create().start(port);

        JavalinValidation.register(Instant.class, s -> Instant.ofEpochMilli(Long.parseLong(s)));

        app.get("/ping", ctx -> ctx.result("pong"));
        app.post("/post", ctx -> {
            FormValidator formValidator = new FormValidator(ctx);
            getDeviceWithPrivateKey(formValidator);
            SensorData sensorData = getSensorData(formValidator);
            sensorData.save();
            // TODO: broadcast
        });
        app.patch("/public_key_update", ctx -> {
            FormValidator formValidator = new FormValidator(ctx);
            Device device = getDeviceWithPrivateKey(formValidator);
            device.setPublicKey(formValidator.getPublicKey());
            // TODO: terminate sessions
        });
        app.ws("/live", ws -> {
            ws.onMessage((WsMessageProtocolHandler) ctx -> {
                JSONValidator jsonValidator = JSONValidator.getInstance(ctx.message());
                Device device = getDeviceWithPublicKey(jsonValidator);
                Instant timeInstant = jsonValidator.getTimeInstant();
                liveServer.subscribe(ctx, device.getDeviceSerial());
                ArrayList<SensorData> sensorData = InfluxManager.getData(device.getDeviceSerial(), timeInstant);
                ctx.send(new Gson().toJson(sensorData));
            });
            ws.onError(ctx -> {
            });
            ws.onClose(ctx -> {
            });
        });
        app.exception(ProtocolException.class, ((e, ctx) -> ctx.result(e.getStringCode()).status(e.getStatus())));
    }
}
