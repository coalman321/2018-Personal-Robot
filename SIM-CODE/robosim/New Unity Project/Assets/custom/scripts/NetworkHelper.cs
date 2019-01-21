using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using UnityEngine;

public class NetworkHelper {
    
    private readonly TcpClient robotPing;
    private readonly NetworkStream pingStream;
    private static readonly byte[] ping = {0x0A};
    private int counter = 0;

    private readonly UdpClient listener;
    private string[] data;
    private IPEndPoint groupEP;
    private byte[] raw;
    private string s;
    
    private readonly DataRecorder recorder;
    
    private DataPlayer player;
    private string[] loadedFile;
    
    public Mode mode { get; set; }

    // Start is called before the first frame update
    public NetworkHelper(int port, int timeout, Mode initial, string pingIP) {
        mode = initial;
        switch (mode)
        {
            case Mode.Recording:
                recorder = new DataRecorder(GameController.getInstance().SaveLocation, ".sav", timeout);
                goto case Mode.Networked;
            case Mode.Networked:
                listener = new UdpClient(port);
                groupEP = new IPEndPoint(IPAddress.Any, port);
                robotPing = new TcpClient(pingIP, port + 1);
                pingStream = robotPing.GetStream();
                break;
            case Mode.Playback:
                loadSave(GameController.getInstance().loadedFile);
                break; 
        }
        data = new[] {"0", "0", "0", "0", "0", "0", "0", "0", "0"};
    }
    

    public float getTimeStamp()
    {
        return float.Parse(data[0]);
    }

    public float getX()
    {
        return float.Parse(data[1]);
    }

    public float getZ()
    {
        return float.Parse(data[2]);
    }

    public float getTheta()
    {
        return float.Parse(data[3]);
    }

    public float getProx()
    {
        return float.Parse(data[4]);
    }

    public float getDist()
    {
        return float.Parse(data[5]);
    }

    public float getWrist()
    {
        return float.Parse(data[6]);
    }

    public float getCurrentState()
    {
        return float.Parse(data[7]);
    }

    public float getTotalStates()
    {
        return float.Parse(data[8]);
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
        if (listener.Available > 0)
        {
            // seems to be blocking on accident when robot stops sending data mid run.
            while (listener.Available > 1)
            {
                //void unused packets
                listener.Receive(ref groupEP);
            }

            raw = listener.Receive(ref groupEP);
            s = Encoding.ASCII.GetString(raw);
            data = CSVReader.readCSVLine(s);
            //Debug.Log(string.Format("0: {0} 1: {1} 2: {2} 3: {3} 4: {4} 5: {5} 6: {6} 7: {7} 8: {8}", data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]));
            recorder.update(s);
        }
        else if( counter > 5){
            pingStream.Write(ping, 0, ping.Length);
            pingStream.Flush();
            counter = 0;
        }
        else {
            counter++;
        }
        
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