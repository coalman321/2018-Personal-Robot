using System;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

public class NetworkHelper {
    
    private readonly UdpClient listener;
    private IPEndPoint groupEP;
    private string[] data;
    private string s;
    private byte[] raw;
    
    // Start is called before the first frame update
    public NetworkHelper(int port){
        listener = new UdpClient(port);
        groupEP = new IPEndPoint(IPAddress.Any, port);
    }

    public float getX() {
        Update();
        return float.Parse(data[0]);
    }
    
    public float getY() {
        Update();
        return float.Parse(data[1]);
    }
    
    public float getTheta() {
        Update();
        return float.Parse(data[2]);
    }
    
    public float getProx() {
        Update();
        return float.Parse(data[3]);
    }
    
    public float getDist() {
        Update();
        return float.Parse(data[4]);
    }
    
    public float getWrist() {
        Update();
        return float.Parse(data[5]);
    }

    public int getCurrentState() {
        Update();
        return int.Parse(data[6]);
    }
    
    public int getTotalStates() {
        Update();
        return int.Parse(data[7]);
    }
    
    public void Update(){
        if (listener.Available > 0){
            //handle update
            raw = listener.Receive(ref groupEP);
            s  = System.Text.Encoding.ASCII.GetString(raw);
            data = CSVReader.readCSVLine(s);
        }
    }
}
