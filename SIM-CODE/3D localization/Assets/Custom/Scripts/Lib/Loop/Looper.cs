using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using Debug = UnityEngine.Debug;

namespace Custom.Scripts.Lib.Loop
{
    public class Looper
    {
        private int period;
        private bool running, enableDebug;
        private List<Loop> loops = new List<Loop>();
        private readonly Thread thread;

        public Looper(double period, bool enableDebug = false)
        {
            this.enableDebug = enableDebug;
            this.period = (int) (period * 1000);
            thread = new Thread(updateThread);
        }

        public void register(Loop loop)
        {
            loops.Add(loop);
        }
        
        public void start()
        {
            running = true;
            thread.Start();
        }

        public void stop()
        {
            running = false;
            if(thread.IsAlive) thread.Join(); 
        }

        private void updateThread()
        {
            foreach (Loop loop in loops){loop.onStart();}
            
            Int64 lastTime = 0;
            Int64 time = Stopwatch.GetTimestamp();
            while (running)
            {
                time = Stopwatch.GetTimestamp();

                foreach (Loop loop in loops){loop.onLoop(time);}

                if (enableDebug) Debug.Log($"[Looper] Update operations took {(Stopwatch.GetTimestamp() - time) / 10000} ms" +
                        $"    Sleeping for {period - (int) ((Stopwatch.GetTimestamp() - time) / 10000)} ms");
                
                Thread.Sleep(period - (int) ((Stopwatch.GetTimestamp() - time) / 10000));
                
                if (enableDebug)Debug.Log($"[Looper] Overall loop time took  {(Stopwatch.GetTimestamp() - time) / 10000} ms");

                lastTime = time;
                //Cleanup for next iteration
            }
            
            foreach (Loop loop in loops){loop.onStop();}
        }
    }
}