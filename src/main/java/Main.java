import db.sql.PostgresConnection;
import db.sql.PostgresManager;
import db.timeseries.InfluxClient;
import org.apache.commons.cli.*;
import server.Server;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("psql_url")
                        .desc("Postgres token")
                        .hasArg()
                        .required()
                        .build())
                .addOption(Option.builder("psql_user")
                        .desc("Postgres username")
                        .hasArg()
                        .required()
                        .build())
                .addOption(Option.builder("psql_pass")
                        .desc("Postgres password")
                        .hasArg()
                        .required()
                        .build())
                .addOption(Option.builder("influx_url")
                        .desc("InfluxDB url")
                        .hasArg()
                        .required()
                        .build())
                .addOption(Option.builder("influx_token")
                        .desc("InfluxDB token")
                        .hasArg()
                        .required()
                        .build());
        try {
            CommandLine commandLine = new DefaultParser().parse(options, args);
            String postgresURL = commandLine.getOptionValue("psql_url");
            String postgresUsername = commandLine.getOptionValue("psql_user");
            String postgresPassword = commandLine.getOptionValue("psql_pass");
            PostgresConnection.createInstance(postgresURL, postgresUsername, postgresPassword);
            PostgresManager.createTables();
            String influxURL = commandLine.getOptionValue("influx_url");
            String influxToken = commandLine.getOptionValue("influx_token");
            InfluxClient.createInstance(influxURL, influxToken);
            Server server = Server.getInstance(8080);
            server.start();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("Required options missed", options);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
