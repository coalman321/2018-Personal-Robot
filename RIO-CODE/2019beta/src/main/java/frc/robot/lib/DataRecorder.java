package frc.robot.lib;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DataRecorder<T> {

    ConcurrentLinkedDeque<String> mLinesToGet = new ConcurrentLinkedDeque<>();
    Field[] mFields;
    PrintWriter mWriter = null;

    /**
     * sets up a logger that pulls public data from a data subclass
     * @param filename including path to file
     * @param typeclass the data subclass to read from
     */
    public DataRecorder(String filename, Class<T> typeclass){
        mFields = typeclass.getFields();
        try {
            mWriter = new PrintWriter(filename);
            System.out.println("created print writer");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        StringBuffer line = new StringBuffer();
        for(Field field : mFields){
            if(line.length() != 0){
                line.append("\t");
            }
            line.append(field.getName());
        }
        writeLine(line.toString());
    }

    /**
     * registers an update in values to a queue
     * @param val
     */
    public void add(T val){
        StringBuilder line = new StringBuilder();
        for(Field field : mFields){
            if(line.length() != 0){
                line.append("\t");
            }
            try {
                line.append(field.get(val).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mLinesToGet.add(line.toString());
    }

    /**
     * call periodically to write all updates to file
     * dumps queued updates into file
     */
    public void write() {
        while (true) {
            String toPrint = mLinesToGet.pollFirst();
            if (toPrint == null) {
                break;
            }
            writeLine(toPrint);
        }
        mWriter.flush();
    }

    private void writeLine(String toWrite){
        System.out.println("writing: " + toWrite);
        if(mWriter !=  null){
            mWriter.println(toWrite);
        }
    }
}
