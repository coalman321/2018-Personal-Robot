using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;

public class ReadOptimizedSocketTables
{

    private Thread synchronizer;
    private Dictionary<string, DataStore> socketTable;
    private volatile bool shouldStop;
    private Stopwatch bootTimer;
    
    public ReadOptimizedSocketTables()
    {
        synchronizer = new Thread(updateThread);
        bootTimer = new Stopwatch();
        bootTimer.Start();
    }

    public void stopUpdates()
    {
        shouldStop = true;
        if(synchronizer.IsAlive) synchronizer.Join(); 
        shouldStop = false;
    }

    public void startUpdates()
    {
        socketTable = new Dictionary<string, DataStore>();
        synchronizer.Start();
    }

    public string getString(string key, string defaultValue = default(string))
    {
        DataStore value;
        return socketTable.TryGetValue(key, out value) ? value. : defaultValue;
    }

    public double getNumber(string key, double defaultValue)
    {
        try
        {
            return double.Parse(getString(key, ""));
        }
        catch (FormatException e)
        {
            return defaultValue;
        }
    }

    public bool getBoolean(string key, bool defaultValue)
    {
        try
        {
            return bool.Parse(getString(key, ""));
        }
        catch (FormatException)
        {
            return defaultValue;
        }
    }

    public void putString(string key, string value)
    {
        socketTable.Add(key, new DataStore(Stopwatch.GetTimestamp(), value));
    }

    public void putNumber(string key, double value)
    {
        putString(key, value.ToString());
    }

    public void putBoolean(string key, bool value)
    {
        putString(key, value.ToString());
    }

    private void updateThread()
    {
        while (!shouldStop)
        {
            //synchroize all keys inside the dictionary
            //write outbound
            //read inbound
        }
        
    }
    
    protected class DataStore
    {
        private Int64 lastUpdate { get; }
        private string value { get; }

        public DataStore(Int64 lastUpdate, string value)
        {
            this.lastUpdate = lastUpdate;
            this.value = value;
        }

    }
    
    
}
