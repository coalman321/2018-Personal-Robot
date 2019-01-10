package frc.robot.actions;

import frc.lib.statemachine.Action;
import edu.wpi.first.wpilibj.Timer;

public class DelayAction extends Action{
    
    double t_start = 0, duration = 0;

    public DelayAction(double delay){
        duration = delay;
    }

    public void onStart(){
        t_start = Timer.getFPGATimestamp();
    }

    public void onLoop(){

    }

    public boolean isFinished(){
        return Timer.getFPGATimestamp() > t_start + duration;
    }

    public void onStop(){
        System.out.println("Action was called to start at t = " + t_start);
        System.out.println("Action called to end at t = " + Timer.getFPGATimestamp());
    }

}