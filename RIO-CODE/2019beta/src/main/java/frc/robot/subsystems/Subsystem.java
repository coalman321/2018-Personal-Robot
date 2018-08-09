package frc.robot.subsystems;

import frc.lib.loops.ILooper;

public abstract class Subsystem {

    public void writeToLog(){}

    public abstract void outputTelemetry();

    public abstract void stop();

    public abstract void reset();

    public void registerEnabledLoops(ILooper enabledLooper){

    }

}
