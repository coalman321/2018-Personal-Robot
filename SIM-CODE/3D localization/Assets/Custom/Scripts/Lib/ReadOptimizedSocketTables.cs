using System;
using System.Collections;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Data.Common;
using System.Diagnostics;
using System.Threading;
using Custom.Scripts.Lib.Loop;
using UnityEngine;
using Debug = UnityEngine.Debug;

public class ReadOptimizedSocketTables : Loop
{

    private volatile bool shouldStop;
    private ConcurrentDictionary<string, DataStore> socketTable;
    private readonly SocketTables communication;
    private bool enableDebug;
    private Looper loopSystem;
    private Int64 lastTime;
    
    public ReadOptimizedSocketTables(string serverIP, bool enableDebug = false, int updateRateMs = 100){
        communication = new SocketTables(serverIP, enableDebug);
        loopSystem = new Looper(updateRateMs, enableDebug);
        loopSystem.register(this);
        this.enableDebug = enableDebug;
    }

    public void stopUpdates(){
        loopSystem.stop();
    }

    public void startUpdates(){
        loopSystem.start();
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

    public void putString(string extKey, string value) {
        socketTable.AddOrUpdate(extKey,
            new DataStore(Stopwatch.GetTimestamp(), value),
            (key, store) => {                         
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

    //handler methods for looper subsystem here
    public void onStart(){
        lastTime = 0;
    } 
    
    public void onLoop(Int64 time){    
        //write outbound
        foreach(KeyValuePair<string, DataStore> entry in this.socketTable){
            if(enableDebug) Debug.Log($"[SocketTables][Update] Updating {entry.Key} : {entry.Value.value} needs update: " +
                                      $" {entry.Value.lastUpdate > lastTime}");
            if (entry.Value.lastUpdate > lastTime) {
                if(enableDebug) Debug.Log($"[SocketTables][Update] Writing update for {entry.Key} : {entry.Value.value}");
                communication.putString(entry.Key, entry.Value.value);
            }
        }
        
        //read inbound
        foreach (SocketTables.Response response in communication.getAll())
        {
            socketTable.AddOrUpdate(response.key,
                new DataStore(Stopwatch.GetTimestamp(), response.value),
                (key, store) => {                         
                    //update case
                    store.value = response.value;
                    store.lastUpdate = time;
                    return store;
                });
        }
            
        lastTime = time;
        
    }
    
    public void onStop()
    {
            
    }
    
    //data class for map
    protected class DataStore{
        public Int64 lastUpdate { set; get; }
        public string value { set; get; }

        public DataStore(Int64 lastUpdate, string value){
            this.lastUpdate = lastUpdate;
            this.value = value;
        }

    }
    
    
}
