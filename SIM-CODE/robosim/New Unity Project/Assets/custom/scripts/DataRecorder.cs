using System;
using System.IO;

public class DataRecorder {
    private StreamWriter writer;
    private string recordingDir, fileEx;
    private int timeout;
    private DateTime lastWrite;
    
    public DataRecorder(string recordingDir, string fileEx, int timeout) {
        this.recordingDir = recordingDir;
        this.fileEx = fileEx;
        this.timeout = timeout;
        reset();
    }

    public void update(string toRecord) {
        if (lastWrite.AddSeconds(timeout) > DateTime.Now) {
            writer.WriteLine(toRecord);// handles recent update within some timeout
            lastWrite = DateTime.Now;
        }
        else {
            reset();// otherwise create a new file and begin recording again
            writer.WriteLine(toRecord);
        }
        
    }

    public void reset() {
        writer?.Close();
        writer = new StreamWriter(recordingDir + "\\" + DateTime.Now.ToString("yyyyMMddHHmmss") + fileEx);
    }
}
