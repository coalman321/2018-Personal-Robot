package frc.robot;

import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.lib.loops.Looper;
import frc.lib.util.ReflectingLogger2;
import frc.robot.subsystems.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class SubsystemManager implements ILooper {

    private final List<Subsystem> mAllSubsystems;
    private final List<Subsystem.PeriodicIO> allToLog = new ArrayList<>();
    private List<Loop> mLoops = new ArrayList<>();
    private ReflectingLogger2<Subsystem.PeriodicIO> logger;

    public SubsystemManager(List<Subsystem> allSubsystems){
        mAllSubsystems = allSubsystems;
        mAllSubsystems.forEach((s) -> allToLog.add(s.getLogger()));
        logger = new ReflectingLogger2<>(allToLog);
    }

    public void logTelemetry(){
        allToLog.clear();
        mAllSubsystems.forEach((s) -> allToLog.add(s.getLogger()));
        logger.update(allToLog);
        logger.write();

    }

    public void outputTelemetry(){
        mAllSubsystems.forEach((s) -> s.outputTelemetry());
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
