using System;
using System.Collections;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using Debug = UnityEngine.Debug;

public class ReadOptimizedSocketTables
{

    private readonly Thread synchronizer;
    private volatile bool shouldStop;
    private ConcurrentDictionary<string, DataStore> socketTable;
    private readonly SocketTables communication;
    private bool enableDebug;
    
    public ReadOptimizedSocketTables(string serverIP, bool enableDebug = false){
        synchronizer = new Thread(updateThread);
        communication = new SocketTables(serverIP, enableDebug);
        this.enableDebug = enableDebug;
    }

    public void stopUpdates(){
        shouldStop = true;
        if(synchronizer.IsAlive) synchronizer.Join(); 
        shouldStop = false;
    }

    public void startUpdates(){
        socketTable = new ConcurrentDictionary<string, DataStore>();
        synchronizer.Start();
    }

    public string getString(string key, string defaultValue = default(string)){
        DataStore data;
        return socketTable.TryGetValue(key, out data) ? data.value : defaultValue;
    }

    public double getNumber(string key, double defaultValue){
        try
        {
            return double.Parse(getString(key, ""));
        }
        catch (FormatException e)
        {
            return defaultValue;
        }
    }

    public bool getBoolean(string key, bool defaultValue){
        try
        {
            return bool.Parse(getString(key, ""));
        }
        catch (FormatException)
        {
            return defaultValue;
        }
    }

    public void putString(string key, string value) {
        socketTable.AddOrUpdate(key,
            delegate{
                //Add case
                return new DataStore(Stopwatch.GetTimestamp(), value);
            },
            delegate(string s, DataStore store){
                //update case
                store.value = value;
                store.lastUpdate = Stopwatch.GetTimestamp();
                return store;
            });
    }

    public void putNumber(string key, double value){
        putString(key, value.ToString());
    }

    public void putBoolean(string key, bool value){
        putString(key, value.ToString());
    }

    private void updateThread() {
        Int64 lastTime = Stopwatch.GetTimestamp();
        while (!shouldStop) {
            
            if(Stopwatch.GetTimestamp() - lastTime > 20) continue;

            if (enableDebug) {
                Debug.Log("updating socket tables data");
            }
            
            //write outbound
            foreach(KeyValuePair<string, DataStore> entry in socketTable){
                if(enableDebug) Debug.Log($"updating {entry.Key} : {entry.Value.value} last updated {entry.Value.lastUpdate} & last iteration {lastTime}");
                if (entry.Value.lastUpdate > lastTime) {
                    communication.putString(entry.Key, entry.Value.value);
                }
            }
            
            //read inbound
            foreach (SocketTables.Response response in communication.getAll()) {
                socketTable.AddOrUpdate(response.key,
                    delegate{
                        //Add case
                        return new DataStore(Stopwatch.GetTimestamp(), response.value);
                    },
                    delegate(string s, DataStore store){
                        //update case
                        store.value = response.value;
                        store.lastUpdate = Stopwatch.GetTimestamp();
                        return store;
                    });
            }

            //Cleanup for next iteration
            lastTime = Stopwatch.GetTimestamp();
        }
        
    }
    
    protected class DataStore{
        public Int64 lastUpdate { set; get; }
        public string value { set; get; }

        public DataStore(Int64 lastUpdate, string value){
            this.lastUpdate = lastUpdate;
            this.value = value;
        }

    }
    
    
}
