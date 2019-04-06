package frc.lib.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.util.List;

public class VersionData {

    private static InetAddress driverStationAddress;

    /**
     * reads build data from file and writes to driverstation plus dashboard
     */
    public static void WriteBuildInfoToDashboard() {
        DriverStation.reportWarning("== Robot Name == " + Constants.ROBOT_NAME + "        |Version ID: " + getInfo("VERSION_ID") + "|", false);
        final String COMP_MSG = "WARNING THIS IS THE COMPETITION SOFTWARE CONFIG";
        final String PRAC_MSG = "WARNING THIS IS THE PRACTICE SOFTWARE CONFIG";
        //DriverStation.reportWarning((Constants.IS_COMP_BOT ? COMP_MSG : PRAC_MSG) + (Constants.ENABLE_MP_TEST_MODE ? "! MP TEST MODE IS ENABLED!" : "!"), false);
        SmartDashboard.putString("Build_Info/ID", getInfo("VERSION_ID"));
        SmartDashboard.putString("Build_Info/Author", getInfo("BUILD_AUTHOR"));
        SmartDashboard.putString("Build_Info/DATE", getInfo("BUILD_DATE"));
    }


    /**
     * gets build info from version.dat file
     *
     * @param key data entry to parse for
     * @return data contained by key or empty string if not found
     */
    public static String getInfo(String key) {
        try {
            File version;
            if (RobotBase.isReal()) version = new File(Filesystem.getDeployDirectory(), "version.dat");
            else version = new File(Filesystem.getLaunchDirectory(), "src\\main\\deploy\\version.dat");
            List<String> lines = Files.readAllLines(version.toPath());
            int i = 0;
            int equalsInd = 0;
            for (String line : lines) {
                if (line.indexOf(key) > -1) {
                    i = line.indexOf(key) + key.length();
                    while (line.charAt(i) != '=') {
                        i++;
                    }
                    equalsInd = i;
                    while (line.charAt(i) != ';') {
                        i++;
                    }
                    return line.substring(equalsInd + 1, i);
                }
            }
            DriverStation.reportWarning("Failed to discover " + key + " in Version.dat", false);
            return "";
        } catch (Exception e) {
            DriverStation.reportError("Failed to read version.dat in deploy directory!", e.getStackTrace());
            return "";
        }
    }

    public static InetAddress getDriverStationIP() {
        try {
            ServerSocket sock = new ServerSocket(5801);
            Notifier noti = new Notifier(() -> {
                try {
                    sock.close();
                } catch (IOException e) {
                }
            });
            noti.startSingle(30.00);
            Socket connection = sock.accept();
            driverStationAddress = ((InetSocketAddress) connection.getRemoteSocketAddress()).getAddress();
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                driverStationAddress = InetAddress.getByName("10.41.45.10"); //fallback ip
                DriverStation.reportWarning("Falling back to default IP", false);
            } catch (UnknownHostException e1) {
                // how on earth
            }
        }
        SmartDashboard.putString("DS_IP", driverStationAddress.getHostAddress());
        return driverStationAddress;
    }

}