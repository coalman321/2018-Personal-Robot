package frc.robot;

import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.lib.loops.Looper;
import frc.lib.util.ReflectingLogger;
import frc.robot.subsystems.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class SubsystemManager implements ILooper {

    //TODO test for memory leaks???
    //cant tell if its actually leaking
    //it might be a slow leak and the best GC is turning the power off as they say

    private final List<Subsystem> mAllSubsystems;
    private List<Loop> mLoops = new ArrayList<>();
    private ReflectingLogger<Subsystem.PeriodicIO> logger;

    public SubsystemManager(List<Subsystem> allSubsystems){
        mAllSubsystems = allSubsystems;

        //get all subsystems to log from
        final List<Subsystem.PeriodicIO> allToLog = new ArrayList<>();
        mAllSubsystems.forEach((s) -> allToLog.add(s.getLogger()));

        //create reflecting logger
        logger = new ReflectingLogger<>(allToLog);
    }

    public void logTelemetry(){
        // create current list of subsystem IO
        //something to do with regenerating this list and map is leaking about .1mb/s
        final List<Subsystem.PeriodicIO> allToLog = new ArrayList<>();
        mAllSubsystems.forEach((s) -> allToLog.add(s.getLogger()));

        //update the logger from the current form of the list
        logger.update(allToLog);
    }

    public void outputTelemetry(){
        mAllSubsystems.forEach(Subsystem::outputTelemetry);
    }

    private class EnabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {
            for (Loop l : mLoops) {
                l.onStart(timestamp);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem s : mAllSubsystems) {
                s.readPeriodicInputs();
            }
            for (Loop l : mLoops) {
                l.onLoop(timestamp);
            }
            for (Subsystem s : mAllSubsystems) {
                s.writePeriodicOutputs();
            }

            //run logging pass
            logTelemetry();

        }

        @Override
        public void onStop(double timestamp) {
            for (Loop l : mLoops) {
                l.onStop(timestamp);
            }
        }



    }

    private class DisabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {

        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem s : mAllSubsystems) {
                s.readPeriodicInputs();
            }
            for (Subsystem s : mAllSubsystems) {
                s.writePeriodicOutputs();
            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    }

    public void registerEnabledLoops(Looper enabledLooper) {
        mAllSubsystems.forEach((s) -> s.registerEnabledLoops(this));
        enabledLooper.register(new EnabledLoop());
    }

    public void registerDisabledLoops(Looper disabledLooper) {
        disabledLooper.register(new DisabledLoop());
    }

    @Override
    public void register(Loop loop) {
        mLoops.add(loop);
    }

}
