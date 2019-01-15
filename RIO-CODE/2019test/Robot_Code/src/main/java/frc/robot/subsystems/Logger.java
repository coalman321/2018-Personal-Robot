package frc.robot.subsystems;

import edu.wpi.first.networktables.ConnectionInfo;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.robot.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger extends Subsystem {

    private static final Logger M_LOGGER = new Logger();

    private File base;
    private PrintWriter printWriter;
    private DatagramSocket sock;
    private DatagramPacket toSend;
    private InetAddress driverStation;
    private List<String> numberKeys, stringKeys, dsKeys;
    private String toWrite;
    private boolean initSuccess;

    private final int port = 5800;

    private Loop mLoop = new Loop(){

        @Override
        public void onStart(double timestamp) {
            if(printWriter == null || !initSuccess){
                DriverStation.reportWarning("logger called to init on Null file stream", false);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            if (printWriter != null && initSuccess) { //probably redundant
                toWrite = "" + Timer.getFPGATimestamp() + Constants.DATA_SEPERATOR;
                for (String key : numberKeys) {
                    toWrite += SmartDashboard.getNumber(key, 0.0) + Constants.DATA_SEPERATOR;
                }
                for (String key : stringKeys) {
                    toWrite += SmartDashboard.getString(key, " ") + Constants.DATA_SEPERATOR;
                }
                toWrite += "\r\n";
                //System.out.println(toWrite);
                printWriter.write(toWrite);
                printWriter.flush();

            }
            if(sock != null && initSuccess){
                toWrite = "" + Timer.getFPGATimestamp() + Constants.DATA_SEPERATOR;
                for (String key : dsKeys) {
                    toWrite += SmartDashboard.getString(key, " ") + Constants.DATA_SEPERATOR;
                }
                toSend.setData(toWrite.getBytes());
                try {
                    sock.send(toSend);
                } catch (IOException e) {
                    DriverStation.reportError("Failed to send packet due to exception!", e.getStackTrace());
                }
            }
        }

        @Override
        public void onStop(double timestamp) {
            if(printWriter == null || !initSuccess){
                DriverStation.reportWarning("Results from the last run were not logged due to an initialization error", false);
            }
        }
    };

    private Logger() {
        numberKeys = new ArrayList<>();
        stringKeys = new ArrayList<>();
        try {
            base = getMount();
            System.out.println(base.getAbsolutePath());
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(base)));

            ConnectionInfo[] remoteTables = NetworkTableInstance.getDefault().getConnections();
            if(remoteTables.length > 0) driverStation = InetAddress.getByName(remoteTables[0].remote_ip);
            else driverStation = InetAddress.getByName("10.41.45.3");
            sock = new DatagramSocket(port);
            toSend = new DatagramPacket(new byte[10], 10, driverStation, port);

            initSuccess = true;
        } catch (Exception e) {
            DriverStation.reportError("No valid logging path detected. Logger stopped", false);
            initSuccess = false;
        }
    }

    public static Logger getInstance() {
        return M_LOGGER;
    }

    public void addNumberKeys(String[] keys) {
        Collections.addAll(numberKeys, keys);
    }

    public void addStringKeys(String[] keys) {
        Collections.addAll(stringKeys, keys);
    }

    public void addDSKeys(String[] keys){
        Collections.addAll(dsKeys, keys);
    }

    private File getMount() {
        File media = new File("/media");
        File logging_path = null;
        for(File mount : media.listFiles())
        {
            logging_path = new File(mount.getAbsolutePath() + "/logging");
            if(logging_path.isDirectory()) {
                System.out.println(logging_path.getAbsolutePath());
                break;
            }
            logging_path = null;
        }
        if (!logging_path.equals(null)) {
            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            outputFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
            String newDateString = outputFormatter.format(new Date());
            // build the new filename
            String fileName = newDateString + "_LOG.tsv";
            // build the full file path name
            return new File(logging_path.getAbsolutePath() + File.separator + fileName);
        }
        return null;
    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(mLoop);
    }

    public void outputTelemetry() {
        // no OP
    }

    @Override
    public void stop() {
        // no OP
    }

    @Override
    public void reset() {
        // no OP
    }

}