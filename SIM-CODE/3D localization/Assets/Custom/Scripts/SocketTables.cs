using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.RegularExpressions;
using Debug = UnityEngine.Debug;

public class SocketTables {

    private static readonly int timeout = 50;
    private static readonly int port = 7777;
    private static readonly string KEY_VALUE_FORMAT = "\"?([A-Za-z\\d_\\-\\.\\s]+)\"?";
    
    private Socket client;
    private IPAddress serverAddr;
    private bool enableDebug;
    private readonly Regex KEY_PATTERN = new Regex("\"key\":\\s*" + KEY_VALUE_FORMAT);
    private readonly Regex VALUE_PATTERN = new Regex("\"value\":\\s*" + KEY_VALUE_FORMAT);
    private readonly Regex KEY_VALUE_PATTERN = new Regex(KEY_VALUE_FORMAT);
    

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
        Response resp = processMessage(rq)?[0];
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

    public List<Response> getAll()
    {
        Request rq = new Request();
        rq.request = RequestType.GETALL.ToString();
        rq.key = "";
        rq.value = "";
        return processMessage(rq);
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
                Debug.Log($"[SocketTables][Core] Socket connected to {client.RemoteEndPoint}");
                Debug.Log($"[SocketTables][Core] Sending message {message}");
            }

            List<Response> resp;

            using (NetworkStream stream = new NetworkStream(client)) {
                new DataContractJsonSerializer(typeof(Request)).WriteObject(stream, message);
                stream.Flush();
            }

            byte[] recieved = new byte[1024]; 
            int bytesrcv = client.Receive(recieved);
            string responseMessage = Encoding.ASCII.GetString(recieved,0,bytesrcv); 
            resp = processGetAll(responseMessage);

            client.Close();
            return resp;
        }

        Debug.Log("[SocketTables][Core] Socket tables connection timed out");
        return new List<Response>();
    }

    private List<Response> processGetAll(string data){
        List<Response> responses = new List<Response>();
        MatchCollection keys = KEY_PATTERN.Matches(data);
        MatchCollection values = VALUE_PATTERN.Matches(data);
        
        for (int i = 0; i < keys.Count; i++)
        {
            //uhhhh wat?
            Match key = keys[i];
            Match value = values[i];
            
            GetAllResponse resp = new GetAllResponse();
            resp.key = key.ToString();
            resp.key = resp.key.Substring(resp.key.IndexOf(": \"") + 3).Trim('\"');
            resp.value = value.ToString();
            resp.value = resp.value.Substring(resp.value.IndexOf(": \"") + 3).Trim('\"');
            resp.timestamp = Stopwatch.GetTimestamp();
            
            if (enableDebug) {
                Debug.Log($"[SocketTables][Core] Response Key : \"{resp.key}\" Value : \"{resp.value}\"");
            }
            
            responses.Add(resp);
        }

        return responses;
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
    
    public class Response
    {
        public string key { get; set; } = "";
        public string value { get; set; } = "";

        public override string ToString()
        {
            return $"{key} : {value}";
        }
    }

    public class GetAllResponse : Response
    {
        public Int64 timestamp { get; set; }

        public override string ToString()
        {
            return $"{key} : {value} modified {timestamp}";
        }

    }

}
