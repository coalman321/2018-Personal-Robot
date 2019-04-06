package frc.lib.util;

import java.net.Socket;

public class SocketTables {

    private static final SocketTables m_Instance = new SocketTables();
    private static final String SOCKET_TABLES_IP = "";

    public static SocketTables getInstance(){
        return m_Instance;
    }

    private Socket client;

    private SocketTables(){
        client = new Socket(SOCKET_TABLES_IP, 7777);

    }

}
