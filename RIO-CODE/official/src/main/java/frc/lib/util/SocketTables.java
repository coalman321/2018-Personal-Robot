package frc.lib.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketTables {

    private static final SocketTables m_Instance = new SocketTables();
    private static final String SOCKET_TABLES_IP = "127.0.0.1";

    private Socket client;
    private SocketAddress serverAddress;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private String snowflake;

    private SocketTables() {
        serverAddress = new InetSocketAddress(SOCKET_TABLES_IP, 7777);

        //Request snowflake & check into system
    }

    public static SocketTables getInstance() {
        return m_Instance;
    }

    public void putData(String key, String data){
        //update server key with new data
        //regenerate last checkin time on the server with the snowflake
        queryValue("{\"request\": \"UPDATE\", \"key\": \"" + key + "\", \"value\": " + data + "}");
    }

    public String getData(String key){
        return queryValue("{\"request\": \"GET\", \"key\": \"" + key + "\"}");
    }



    private String queryValue(String request) {
        try {
            //setup connection
            client = new Socket();
            client.connect(serverAddress);
            outToServer = new DataOutputStream(client.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));

            //write query
            outToServer.write(request.getBytes());
            outToServer.flush();

            //read response
            final String query = inFromServer.readLine();

            //close IO
            client.close();
            outToServer.close();
            inFromServer.close();

            return query;
        } catch (IOException e) {
            System.out.println("Query failed");
            e.printStackTrace();
        }
        return null;
    }

    private void close() {

    }

    private enum DataOperation {

        GET("GET"),
        GETALL("GETALL"),
        UPDATE("UPDATE"),
        DELETE("DELETE");

        private DataOperation(String opcode) {

        }

    }

}
