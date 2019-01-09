package frc.lib.statemachine;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class StateMachine {

    private volatile static AtomicInteger state = new AtomicInteger(-1);
    private volatile static ConcurrentLinkedQueue<ActionGroup> queuedStates;
    private volatile static ActionGroup currentState;
    private volatile static double t_start;
    private static Runnable Man = () -> {
        try {
            state.set(0);
            SmartDashboard.putNumber("StateMachine/ state", state.get());
            if (queuedStates == null) {
                state.set(-2);
                SmartDashboard.putNumber("StateMachine/ state", state.get());
            } else {
                while (!queuedStates.isEmpty()) {
                    SmartDashboard.putNumber("StateMachine/ state", state.get());
                    currentState = queuedStates.poll();
                    currentState.onStart();
                    while (!currentState.isFinished()) {
                        t_start = Timer.getFPGATimestamp();
                        currentState.onLoop();
                        Timer.delay(0.02 - (Timer.getFPGATimestamp() - t_start));
                    }
                    currentState.onStop();
                    state.getAndAdd(1);

                }
            }
        }catch (Exception e){
            state.set(-3);
            SmartDashboard.putNumber("StateMachine/ state", state.get());
        }
    };

    public static void runMachine(StateMachineDescriptor descriptor) {
        queuedStates = descriptor.getStates();
        Thread thread = new Thread(Man);
        thread.start();

    }


}
