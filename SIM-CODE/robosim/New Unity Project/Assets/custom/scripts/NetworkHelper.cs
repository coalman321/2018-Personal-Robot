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
        data = new string[9];
    }

    public float getTimeStamp() {
        return float.Parse(data[0]);
    }
    
    public float getX() {
        return float.Parse(data[1]);
    }
    
    public float getY() {
        return float.Parse(data[2]);
    }
    
    public float getTheta() {
        return float.Parse(data[3]);
    }
    
    public float getProx() {
        return float.Parse(data[4]);
    }
    
    public float getDist() {
        return float.Parse(data[5]);
    }
    
    public float getWrist() {
        return float.Parse(data[6]);
    }

    public int getCurrentState() {
        return int.Parse(data[7]);
    }
    
    public int getTotalStates() {
        return int.Parse(data[8]);
    }
    
    public void update(){
        if (listener.Available > 0){
            //handle update
            raw = listener.Receive(ref groupEP);
            s  = System.Text.Encoding.ASCII.GetString(raw);
            data = CSVReader.readCSVLine(s);
            //Debug.Log(data[0] + " " + data[1]);
        }
    }
}
