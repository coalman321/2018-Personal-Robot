using System.IO;
using System.Linq;

public class DataPlayer
{

    public static string[] getFilesInDir(string dir) {
        return Directory.GetFiles(dir, "*.sav");
    }

    private StreamReader reader;
    private int fileLen;

    public DataPlayer(string filepath) {
        fileLen = File.ReadLines(filepath).Count();
        reader = new StreamReader(filepath);    
    }

    /**
     * this may be a bad idea
     * it will allow for scrolling playback though
     */
    public string[] readIntoMem() {
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
