package frc.lib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ReflectingLogger<T> {

    private Field[] fields;
    private PrintWriter output = null;
    ConcurrentLinkedDeque<String> linesToWrite = new ConcurrentLinkedDeque<>();

    public ReflectingLogger(String fileName, Class<T> typeClass) {
        fields = typeClass.getFields();
        try {
            output = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Write field names.
        StringBuffer line = new StringBuffer();
        for (Field field : fields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            line.append(field.getName());
        }
        writeLine(line.toString());
    }

    public void update(T value) {
        StringBuffer line = new StringBuffer();
        for (Field field : fields) {
            if (line.length() != 0) {
                line.append(", ");
            }
            try {
                if (CSVWritable.class.isAssignableFrom(field.getType())) {
                    line.append(((CSVWritable) field.get(value)).toCSV());
                } else {
                    line.append(field.get(value).toString());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        linesToWrite.add(line.toString());
    }

    protected synchronized void writeLine(String line) {
        System.out.println(line);
        if (output != null) {
            output.println(line);
        }
    }

    // Call this periodically from any thread to write to disk.
    public void write() {
        while (true) {
            String val = linesToWrite.pollFirst();
            if (val == null) {
                break;
            }
            writeLine(val);
        }
    }

    public static File getMount() {
        File media = new File("/media");
        File logging_path = null;
        for (File mount : media.listFiles()) {
            logging_path = new File(mount.getAbsolutePath() + "/logging");
            if (logging_path.isDirectory()) {
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

}
