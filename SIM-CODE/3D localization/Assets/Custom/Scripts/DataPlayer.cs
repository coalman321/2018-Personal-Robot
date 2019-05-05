using System.IO;
using System.Linq;

public class DataPlayer
{

    public static string[] getFilesInDir(string dir) {
        return Directory.GetFiles(dir, "*.sav");
    }

    /**
     * this may be a bad idea
     * it will allow for scrolling playback though
     */
    public static string[] readIntoMem(string filepath) {
        int fileLen = File.ReadLines(filepath).Count();
        StreamReader reader = new StreamReader(filepath); 
        string[] store = new string[fileLen];
        int index = 0;
        while (!reader.EndOfStream) {
            store[index] = reader.ReadLine();
            index++;
        }
        reader.Close();
        return store;
    }
    
}
