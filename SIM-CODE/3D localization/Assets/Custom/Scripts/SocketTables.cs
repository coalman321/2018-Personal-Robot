using System;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Text;
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

    public void update(string key, string value) {
        //form request
        Request rq = new Request();
        rq.request = RequestType.UPDATE.ToString();
        rq.key = key;
        rq.value = value;

        processMessage(rq);
    }

    public string query(string key) {
        Request rq = new Request();
        rq.request = RequestType.GET.ToString();
        rq.key = key;
        rq.value = "";
        Response resp = processMessage(rq);
        return resp.value;
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
