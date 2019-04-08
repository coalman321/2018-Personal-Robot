package frc.lib.util;

import edu.wpi.first.wpilibj.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ReflectingLogger<T> {

    //TODO test for memory leaks???
    //cant tell if its actually leaking
    //it might be a slow leak and the best GC is turning the power off as they say

    private PrintWriter output = null;
    private Map<Field, T> classFieldMap = new LinkedHashMap<>();

    public ReflectingLogger(List<T> subsystemIOs) {
        //generate map of subsystem IO's and fields
        for(T subsystemIO : subsystemIOs){
            for(Field field : subsystemIO.getClass().getFields()) {
                classFieldMap.put(field, subsystemIO);
            }
        }

        //create file reference
        try {
            File logfile = getMount("robotdata");
            if (logfile != null) output = new PrintWriter(logfile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write field names.
        StringBuffer line = new StringBuffer();
        line.append("time");
        for (Map.Entry<Field, T> entry : classFieldMap.entrySet()) {
            line.append(", ");
            line.append(entry.getKey().getName());
        }
        writeLine(line.toString());
    }

    public void update(List<T> subsystemIOs) {
        final StringBuffer line = new StringBuffer();
        //generate map of subsystem IO's and fields
        //TODO figure out why this code causes a memory leak
        /*for(T subsystemIO : subsystemIOs){
            for(Field field : subsystemIO.getClass().getFields()) {
                classFieldMap.put(field, subsystemIO);
            }
        }*/

        //Append starting time
        line.append(" " + Timer.getFPGATimestamp());

        //for all fields in map generate
        for (Map.Entry<Field, T> entry : classFieldMap.entrySet()) {

            //append separator
            line.append(", ");

            //Attempt to append subsystem IO value
            try {
                if (CSVWritable.class.isAssignableFrom(entry.getKey().getType())) {
                    line.append(((CSVWritable) entry.getKey().get(entry.getValue())).toCSV());
                } else {
                    line.append(entry.getKey().get(entry.getValue()).toString());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        writeLine(line.toString());
    }

    protected synchronized void writeLine(String line) {
        //System.out.println(line);
        if (output != null) {
            output.println(line);
        }
    }

    public static File getMount(String subsystemName) {
        //create base file reference looking for the media directory
        File media = new File("/media");
        if (!media.exists()) return null;

        //Locate the currently active media drive by finding a nested logging directory
        File logging_path = null;
        for (File mount : media.listFiles()) {
            logging_path = new File(mount.getAbsolutePath() + "/logging");
            if (logging_path.isDirectory()) {
                System.out.println(logging_path.getAbsolutePath());
                break;
            }
            logging_path = null;
        }

        //if a valid logging directory is found return a non-null file refrence
        if (logging_path != null) {
            // build the full file path name
            return new File(logging_path.getAbsolutePath() + File.separator + getTimeStampedFileName(subsystemName));
        }

        return null;
    }

    private static String getTimeStampedFileName(String subsystemName){
        SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        outputFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        String newDateString = outputFormatter.format(new Date());
        return subsystemName + "_" + newDateString + "_LOG.csv";
    }

}
