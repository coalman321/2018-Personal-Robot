using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using UnityEngine;

public class NetworkHelper {
    
    private DateTime lastSuccess;
    
    private readonly DataRecorder recorder;
    
    private DataPlayer player;
    private string[] loadedFile;
    
    public Mode mode { get; set; }

    // Start is called before the first frame update
    public NetworkHelper(int port, string pingIP, int timeout, Mode initial) {
        mode = initial;
        switch (mode)
        {
            case Mode.Recording:
                recorder = new DataRecorder(GameController.getInstance().SaveLocation, ".sav", timeout);
                goto case Mode.Networked;
            case Mode.Networked:
                
                break;
            case Mode.Playback:
                loadSave(GameController.getInstance().loadedFile);
                break; 
        }
    }
    

    public float getTimeStamp()
    {
        return 0;
    }

    public float getX()
    {
        return 0;
    }

    public float getZ()
    {
        return 0;
    }

    public float getTheta()
    {
        return 0;      
    }

    public float getProx()
    {
        return 0;      
    }

    public float getDist()
    {
        return 0;      
    }

    public float getWrist()
    {
        return 0;       
    }

    public float getCurrentState()
    {
        return 0; 
    }

    public float getTotalStates()
    {
        return 0;
    }

    public void update(int frame)
    {
        switch (mode) {
            case Mode.Networked:
                refreshNetworkData();
                break;
            case Mode.Recording:
                refreshNetworkData();
                break;
            case Mode.Playback:
                refreshFileData(frame);
                break;
        }
    }

    private void refreshNetworkData() {
        
    }

    private void refreshFileData(int frame) {
        if (loadedFile == null) throw new FileLoadException("File data was not loaded before attempting playback");
        s = loadedFile[Clamp(frame, 0, loadedFile.Length - 1)];
        data = CSVReader.readCSVLine(s);
    }

    public int loadSave(string file) {
        loadedFile = DataPlayer.readIntoMem(file);
        return loadedFile.Length - 1;
    }

    public static string[] getSaves()
    {
        return DataPlayer.getFilesInDir(GameController.getInstance().SaveLocation);
    }
    
    public static int Clamp( int value, int min, int max )
    {
        return (value < min) ? min : (value > max) ? max : value;
    }

    public enum Mode {
        Networked,
        Recording,
        Playback,
        Disconnected
    }
}