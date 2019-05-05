using System;
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
        Response resp = processMessage(rq);
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

    public void delete(string key) {
        //form request
        Request rq = new Request();
        rq.request = RequestType.DELETE.ToString();
        rq.key = key;
        rq.value = "";

        processMessage(rq);
    }

    private Response processMessage(Request message) {
        //setup connection
        client = new Socket(serverAddr.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        if (client.ConnectAsync(serverAddr, port).Wait(1000)) {
            if (enableDebug) {
                Debug.Log($"Socket connected to {client.RemoteEndPoint}");
                Debug.Log($"Sending message {message}");
            }

            DataContractJsonSerializer encoder = new DataContractJsonSerializer(typeof(Request));
            DataContractJsonSerializer decoder = new DataContractJsonSerializer(typeof(Response));
            Response resp;

            using (NetworkStream stream = new NetworkStream(client)) {
                encoder.WriteObject(stream, message);
                stream.Flush();
                resp = (Response)decoder.ReadObject(stream);
            }
            
            if(enableDebug) Debug.Log($"response: {resp.key} : {resp.value}");
            
            client.Close();
            return resp;
        }

        Debug.Log("Socket tables connection timed out");
        return new Response();
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

    enum RequestType {
        GET,
        GETALL,
        UPDATE,
        DELETE
    }
    
    [DataContract]
    internal class Response {
        [DataMember] internal string key = "";
        [DataMember] internal string value = "";
    }

}
