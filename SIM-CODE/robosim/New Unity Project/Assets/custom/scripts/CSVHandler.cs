using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;


public class CSVWriter {

    private FileStream fs;
    
    public CSVWriter(string filepath) {
        fs = File.Create(filepath + "\\" + GetTimestamp(DateTime.Now));
    }
    
    public CSVWriter(string filepath, string header) {
        fs = File.Create(filepath + "\\" + GetTimestamp(DateTime.Now));
        writeLine(header);
    }

    public void writeData(string[] data) {
        string combination = data[0];
        if (data.Length > 1) {
            for (int i = 1; i < data.Length; i++) {
                combination += "," + data[i];
            }
        }
        writeLine(combination);
    }
    
    public void writeLine(string toWrite) {
        write(toWrite + "\n");
    }

    public void write(string toWrite) {
        byte[] data = new UTF8Encoding(true).GetBytes(toWrite);
        fs.Write(data, 0, data.Length);
    }
    
    private static String GetTimestamp(DateTime value) {
        return value.ToString("yyyyMMddHHmmssffff");
    }
    
}

public class CSVReader{

    public static string[][] readCSVFile(string filepath) {
        string[] fileContents = File.ReadAllLines(filepath);
        string[][] toRet = new string[fileContents.Length][];
        for (int i = 0; i < fileContents.Length; i++) {
            toRet[i] = readCSVLine(fileContents[i]);
        }

        return toRet;
    }
    
    public static string[] readCSVLine(string line) {
        return line.Split(',');
    }
}
