using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using UnityEngine;

public class SocketTables {

    private static readonly int timeout = 50;
    private static readonly int port = 7777;
    
    private Socket client;
    private IPAddress serverAddr;
    private bool enableDebug;
    

    public SocketTables(string serverAddress, bool enableDebug = false) {
         serverAddr = IPAddress.Parse(serverAddress);
        this.enableDebug = enableDebug;
    }

    //base method for updating a server value
    private void update(string key, string value) {
        //form request
        Request rq = new Request();
        rq.request = RequestType.UPDATE.ToString();
        rq.key = key;
        rq.value = value;

        processMessage(rq);
    }

    public void putNumber(string key, double value) {
        update(key, value.ToString());
    }

    public void putBoolean(string key, bool value) {
        update(key, value.ToString());
    }

    public void putString(string key, string value) {
        update(key, value);
    }

    //base method for retrieving a server value
    private string query(string key, string defaultValue) {
        Request rq = new Request();
        rq.request = RequestType.GET.ToString();
        rq.key = key;
        rq.value = "";
        Response resp = processMessage(rq)[0];
        if (resp.value.Equals("")) return defaultValue;
        return resp.value;
    }

    public double getNumber(string key, double defaultValue) {
        try {
            return int.Parse(query(key, defaultValue.ToString()));
        }
        catch (FormatException e) {
            return defaultValue;
        }
    }

    public bool getBoolean(string key, bool defaultValue) {
        try {
            return bool.Parse(query(key, defaultValue.ToString()));
        }
        catch (FormatException e) {
            return defaultValue;
        }
    }

    public string getString(string key, string defaultValue) {
        return query(key, defaultValue);
    }

    public void getAll()
    {
        Request rq = new Request();
        rq.request = RequestType.GETALL.ToString();
        rq.key = "";
        rq.value = "";
        Debug.Log(processMessage(rq).Count);
    }

    public void delete(string key) {
        //form request
        Request rq = new Request();
        rq.request = RequestType.DELETE.ToString();
        rq.key = key;
        rq.value = "";

        processMessage(rq);
    }

    private List<Response> processMessage(Request message) {
        //setup connection
        client = new Socket(serverAddr.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        if (client.ConnectAsync(serverAddr, port).Wait(1000)) {
            if (enableDebug) {
                Debug.Log($"Socket connected to {client.RemoteEndPoint}");
                Debug.Log($"Sending message {message}");
            }

            List<Response> resp;

            using (NetworkStream stream = new NetworkStream(client)) {
                new DataContractJsonSerializer(typeof(Request)).WriteObject(stream, message);
                stream.Flush();
                if (message.request.Equals(RequestType.GETALL.ToString()))
                {
                    resp = (List<Response>) new DataContractJsonSerializer(typeof(List<GetAllResponse>)).ReadObject(stream);
                }
                else
                {
                    resp = new List<Response>(new [] {(Response) new DataContractJsonSerializer(typeof(Response)).ReadObject(stream)});
                }
                
            }
            
            if(enableDebug)
                foreach (Response response in resp){
                    Debug.Log($"response: {response.key} : {response.value}");
                }

            
            
            client.Close();
            return resp;
        }

        Debug.Log("Socket tables connection timed out");
        return new List<Response>();
    }

    enum RequestType {
        GET,
        GETALL,
        UPDATE,
        DELETE
    }

    [DataContract]
    internal class Request {
        
        [DataMember] internal string request;
        [DataMember] internal string key;
        [DataMember] internal string value;

        public override string ToString() {
            return $"{request} : {key} : {value}";
        }
    }
    
    [DataContract]
    internal class Response
    {
        [DataMember] internal string key { get; set; } = "";
        [DataMember] internal string value { get; set; } = "";

        public override string ToString()
        {
            return $"{key} : {value}";
        }
    }

    [DataContract]
    internal class GetAllResponse : Response
    {
        [DataMember] internal string key { get; set; }
        [DataMember] internal string timestamp { get; set; } = "";

        public override string ToString()
        {
            return $"{key} : {value} modified {timestamp}";
        }

        //{"test2": {"value": 53, "timestamp": "2019-05-09 16:13:18"}, "test1": {"value": 3, "timestamp": "2019-05-09 16:13:18"}}

    }

}
