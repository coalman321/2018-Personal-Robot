using System;

namespace Custom.Scripts.Lib.Loop{
    public interface Loop
    {

        void onStart();

        void onLoop(Int64 time);

        void onStop();

    }
}