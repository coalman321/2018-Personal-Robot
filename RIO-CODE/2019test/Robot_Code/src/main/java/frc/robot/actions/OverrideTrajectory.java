package frc.robot.actions;

import frc.lib.statemachine.Action;
import frc.robot.subsystems.Drive;

public class OverrideTrajectory extends Action {

    @Override
    public void onStart() {
        Drive.getInstance().overrideTrajectory(true);
    }

    @Override
    public void onLoop() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void onStop() {

    }
}
