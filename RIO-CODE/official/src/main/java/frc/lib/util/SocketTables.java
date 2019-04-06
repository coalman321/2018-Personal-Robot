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
    private boolean initSuccess = false;
    private SocketTables() {
        try {
            client = new Socket();
            serverAddress = new InetSocketAddress(SOCKET_TABLES_IP, 7777);
            client.connect(serverAddress);
            outToServer = new DataOutputStream(client.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            initSuccess = true;
        } catch (IOException except) {
            System.out.println("SocketTables initialization failure");
            except.printStackTrace();
        }

    }

    public static SocketTables getInstance() {
        return m_Instance;
    }

    public String queryValue(String request) {
        System.out.println(client.isConnected());
        if (!initSuccess) return null;
        try {
            if (!client.isConnected()) client.connect(serverAddress);
            writeSocket(request);
            final String query = readSocket();
            System.out.println("query succeeded");
            return query;
        } catch (IOException e) {
            System.out.println("Query failed");
            e.printStackTrace();
        }
        return null;
    }

    private String readSocket() throws IOException {
        return inFromServer.readLine();
    }

    private void writeSocket(String toWrite) throws IOException {

        outToServer.write(toWrite.getBytes());
        outToServer.flush();

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
