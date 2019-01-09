package frc.lib.statemachine;

import edu.wpi.first.wpilibj.command.Command;

public abstract class Action {

    private boolean hasStopped = false;

    public abstract void onStart();

    public abstract void onLoop();

    public abstract boolean isFinished();

    public abstract void onStop();

    public void doStop(){
        if(!hasStopped){
            onStop();
            hasStopped = true;
        }
    }

    /**
     * converts an action to a wpilib command for buttons
     */
    public static Command toCommand(Action action){
        return new Command() {

            protected void initialize(){
                action.onStart();
            }

            protected void execute(){
                action.onLoop();
            }

            protected boolean isFinished() {
                return action.isFinished();
            }

            protected void end(){
                action.onStop();
            }
        };
    }
}
