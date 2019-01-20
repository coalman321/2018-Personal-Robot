using System;
using System.IO;
using UnityEngine;

public class DataRecorder {
    private StreamWriter writer;
    private string recordingDir, fileEx;
    private int timeout;
    private DateTime lastWrite;
    
    public DataRecorder(string recordingDir, string fileEx, int timeout) {
        this.recordingDir = recordingDir;
        this.fileEx = fileEx;
        this.timeout = timeout;
        lastWrite = DateTime.Now.Subtract(TimeSpan.FromSeconds(timeout + 1)); // force timeout condition
    }

    public void update(string toRecord) {
        //Debug.Log(string.Format("Now: {0}, last write: {1}", DateTime.Now, lastWrite));
        if (lastWrite.AddSeconds(timeout) > DateTime.Now) {
            writer.WriteLine(toRecord);// handles recent update within some timeout
            writer.Flush();
            lastWrite = DateTime.Now;
        }
        else {
            reset();// otherwise create a new file and begin recording again
            writer.WriteLine(toRecord);
            writer.Flush();
            lastWrite = DateTime.Now;
        }
        
    }

    public void reset() {
        writer?.Close();
        writer = new StreamWriter(recordingDir + "\\" + DateTime.Now.ToString("MMMdddyyyyHHmmss") + fileEx);
    }
}
