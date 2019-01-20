using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;

public class NetworkHelper
{
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
    public NetworkHelper(int port, string saveDir, int timeout, Mode initial)
    {
        listener = new UdpClient(port);
        groupEP = new IPEndPoint(IPAddress.Any, port);
        data = new[] {"0", "0", "0", "0", "0", "0", "0", "0", "0"};
        
        recorder = new DataRecorder(saveDir, ".sav", timeout);
        mode = initial;

        
    }
    

    public float getTimeStamp()
    {
        return float.Parse(data[0]);
    }

    public float getX()
    {
        return float.Parse(data[1]);
    }

    public float getY()
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

    public int getCurrentState()
    {
        return int.Parse(data[7]);
    }

    public int getTotalStates()
    {
        return int.Parse(data[8]);
    }

    public void update(int frame)
    {
        switch (mode) {
            case Mode.Networked:
                refreshNetworkData();
                break;
            case Mode.Recording:
                refreshNetworkData();
                recorder.update(s);
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
            //Debug.Log(data[0] + " " + data[1]);
        }
    }

    private void refreshFileData(int frame) {
        if (loadedFile == null) throw new FileLoadException("File data was not loaded before attempting playback");
        s = loadedFile[Clamp(frame, 0, loadedFile.Length - 1)];
        data = CSVReader.readCSVLine(s);
    }

    public int loadSave(string file) {
        player = new DataPlayer(file);
        loadedFile = player.readIntoMem();
        return loadedFile.Length - 1;
    }
    
    public static int Clamp( int value, int min, int max )
    {
        return (value < min) ? min : (value > max) ? max : value;
    }

    public enum Mode {
        Networked,
        Recording,
        Playback
    }
}