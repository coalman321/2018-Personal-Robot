package org.usfirst.frc.team7303.shared;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team7303.robot.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoggingSystem {

    public class LocalNum {

        double stored;

        public LocalNum(){
            stored = 0.0;
        }

        public void set(double toSet){
            stored = toSet;
        }

        private double get(){
            return stored;
        }
    }

    public class LocalString {

        String stored;

        public LocalString(){
            stored = "";
        }

        public void set(String toSet){
            stored = toSet;
        }

        private String get(){
            return stored;
        }
    }

    private File base;
    private PrintWriter printWriter;
    private List<String > smartDashKeys;
    private List<LocalNum> localNums;
    private List<LocalString> localStrings;
    private String toWrite;
    private Notifier loggerThread;
    private boolean initSuccess;
    private Runnable runnable = () -> logLine();

    public LoggingSystem(){
        initSuccess = false;
        smartDashKeys = new ArrayList<>();
        localNums = new ArrayList<>();
        localStrings = new ArrayList<>();
        loggerThread = new Notifier(runnable);
        try {
            base = getMount();
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(base)));
            initSuccess = true;
        } catch (Exception e) {
            DriverStation.reportError("Failed to initialize log on file!", false);
            //e.printStackTrace();
        }
    }

    public void addSmartKey(String key){
        smartDashKeys.add(key);
    }

    public void addLocalNum(LocalNum inst){
        localNums.add(inst);
    }

    public void addLocalString(LocalString inst){
        localStrings.add(inst);
    }

    public void enablePrint(boolean enable){
        if(initSuccess) {
            if (enable) {
                loggerThread.startPeriodic(Constants.LOGGING_UPDATE_RATE);
            } else {
                loggerThread.stop();
            }
        }
        else{
            DriverStation.reportWarning("logger called to init on Null file stream", false);
        }
    }

    private void logLine(){
        if(printWriter != null) {
            toWrite = "" + Timer.getFPGATimestamp() + "\t";
            for (String key : smartDashKeys) {
                toWrite += "" + SmartDashboard.getNumber(key, 0.0) + "\t";
            }
            for (LocalNum num : localNums){
                toWrite += "" + num.get() + "\t";
            }
            for (LocalString str : localStrings){
                toWrite += "" + str.get() + "\t";
            }
            toWrite += "\r\n";
            //System.out.println(toWrite);
            printWriter.write(toWrite);
            printWriter.flush();
        }
    }

    private File getMount() {
        File mountPoint;
        // find the mount point
        File testPoint = new File(Constants.DRIVE_PATH_1 + "/logging");
        if (testPoint.isDirectory()) { //robotDriveV4 exists on sda
            mountPoint = testPoint;
        } else {
            testPoint = new File(Constants.DRIVE_PATH_2 + "/logging");
            if (testPoint.isDirectory()) {//robotDriveV4 exists on sdb
                mountPoint = testPoint;
            } else {
                mountPoint = null;
            }
        }
        if (mountPoint != null){
            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            outputFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
            String newDateString = outputFormatter.format(new Date());
            // build the new filename
            String fileName = newDateString + "_LOG.tsv";
            // build the full file path name
            return new File(mountPoint.getAbsolutePath() + File.separator + fileName);
        }
        return null;
    }

    public static void WriteBuildInfoToDashboard() {
        try {
            //get the path of the currently executing jar file
            String currentJarFilePath = Robot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path filePath = Paths.get(currentJarFilePath);

            //get file system details from current file
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            Date utcFileDate = new Date(attr.lastModifiedTime().toMillis());

            // convert from UTC to local time zone
            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
            String newDateString = outputFormatter.format(utcFileDate);

            // write the build date & time to the operator's console log window
            DriverStation.reportWarning("== Robot Name == " + Constants.ROBOT_NAME + "| Build Date and Time: " + newDateString + "|", false);
        } catch (URISyntaxException e) {
            DriverStation.reportWarning("Error determining filename of current JAR file", true);
            //e.printStackTrace();
        } catch (IOException e) {
            DriverStation.reportWarning("General Error trying to determine current JAR file", true);
            //e.printStackTrace();
        }

    }

}
