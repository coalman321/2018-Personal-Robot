using UnityEngine;

public class DataHandler {

    private DataRecorder recorder;
    private int recorderTimeout;
    private string[] loadedFile;
    private string loadedFileName;
    private SocketTables robotTables;
    private string socketTablesAddr;

    private Mode datamode;
    
    
    public DataHandler(string socketTablesAddr, int recorderTimeout) {
        reset(socketTablesAddr, recorderTimeout);
    }

    public void reset(string socketTablesAddr, int recorderTimeout) {
        datamode = Mode.Disconnected;
        this.socketTablesAddr = socketTablesAddr;
        this.recorderTimeout = recorderTimeout;
    }

    public void reset() {
        datamode = Mode.Disconnected;
    }

    public void refreshData(int frame) {
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
                loadedFile = DataPlayer.readIntoMem(loadedFileName);
                datamode = Mode.Playback;
                goto case Mode.Playback;
            case Mode.Playback:
            
                break;
            case Mode.Disconnected: 
                
                break;
        } 
    }

    public void loadSavedFile(string file) {
        loadedFileName = file;
        datamode = Mode.PlaybackUninit;
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
