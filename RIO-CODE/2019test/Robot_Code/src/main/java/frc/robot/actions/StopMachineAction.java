package frc.robot.actions;

import frc.lib.statemachine.Action;
import frc.lib.statemachine.StateMachine;

public class StopMachineAction extends Action{

    public void onStart(){
        System.out.println("Halting State Machine");
        StateMachine.assertStop();
    }

    public void onLoop(){

    }

    public boolean isFinished(){
        return true;
    }

    public void onStop(){

    }

}