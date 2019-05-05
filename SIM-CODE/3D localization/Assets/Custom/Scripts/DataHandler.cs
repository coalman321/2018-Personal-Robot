using System.Collections.Generic;

public class DataHandler {

    private DataRecorder recorder;
    private int recorderTimeout;
    private string[] loadedFile;
    private string loadedFileName;
    private SocketTables robotTables;
    private string socketTablesAddr;

    private Mode datamode;

    private Dictionary<string, double> dataTable;
    
    
    public DataHandler(string socketTablesAddr, int recorderTimeout) {
        reset(socketTablesAddr, recorderTimeout);
    }

    public void reset(string socketTablesAddr, int recorderTimeout) {
        this.socketTablesAddr = socketTablesAddr;
        this.recorderTimeout = recorderTimeout;
        reset();
    }

    public void reset() {
        datamode = Mode.Disconnected;
        dataTable = new Dictionary<string, double>();
        
    }

    //add data to retrieve for system
    public void addData(string key) {
        dataTable.Add(key, 0);
    }

    public Dictionary<string, double> refreshData(int frame) {
        switch (datamode) {
            case Mode.NetworkedUninit:
                robotTables = new SocketTables(socketTablesAddr);
                datamode = Mode.Networked;
                goto case Mode.Networked;
            case Mode.Networked:
                
                break;
            case Mode.RecordingUninit:
                robotTables = new SocketTables(socketTablesAddr);
                recorder = new DataRecorder(GameController.getInstance().SaveLocation, ".sav", recorderTimeout);
                datamode = Mode.Recording;
                goto case Mode.Recording;
            case Mode.Recording:                
                
                break;
            case Mode.PlaybackUninit:
                
                datamode = Mode.Playback;
                goto case Mode.Playback;
            case Mode.Playback:
            
                break;
            case Mode.Disconnected: 
                
                break;
        }

        return dataTable;
    }

    public int loadSavedFile(string file) {
        loadedFileName = file;
        loadedFile = DataPlayer.readIntoMem(loadedFileName);
        datamode = Mode.PlaybackUninit;
        return loadedFile.Length - 1;
    }

    public enum Mode {
        Networked,
        NetworkedUninit,
        Recording,
        RecordingUninit,
        Playback,
        PlaybackUninit,
        Disconnected
    }
}
