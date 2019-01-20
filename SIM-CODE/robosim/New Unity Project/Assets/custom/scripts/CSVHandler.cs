using System;
using System.IO;
using System.Text;


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