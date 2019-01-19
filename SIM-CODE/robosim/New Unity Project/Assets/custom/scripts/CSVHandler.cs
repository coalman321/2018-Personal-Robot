using System;
using System.IO;
using System.Text;

public class CSVWriter
{
    private readonly FileStream fs;

    public CSVWriter(string filepath)
    {
        fs = File.Create(filepath + "\\" + GetTimestamp(DateTime.Now));
    }

    public CSVWriter(string filepath, string header)
    {
        fs = File.Create(filepath + "\\" + GetTimestamp(DateTime.Now));
        writeLine(header);
    }

    public void writeData(string[] data)
    {
        var combination = data[0];
        if (data.Length > 1)
            for (var i = 1; i < data.Length; i++)
                combination += "," + data[i];
        writeLine(combination);
    }

    public void writeLine(string toWrite)
    {
        write(toWrite + "\n");
    }

    public void write(string toWrite)
    {
        var data = new UTF8Encoding(true).GetBytes(toWrite);
        fs.Write(data, 0, data.Length);
    }

    private static string GetTimestamp(DateTime value)
    {
        return value.ToString("yyyyMMddHHmmssffff");
    }
}

public class CSVReader
{
    public static string[][] readCSVFile(string filepath)
    {
        var fileContents = File.ReadAllLines(filepath);
        var toRet = new string[fileContents.Length][];
        for (var i = 0; i < fileContents.Length; i++) toRet[i] = readCSVLine(fileContents[i]);

        return toRet;
    }

    public static string[] readCSVLine(string line)
    {
        return line.Split(',');
    }
}