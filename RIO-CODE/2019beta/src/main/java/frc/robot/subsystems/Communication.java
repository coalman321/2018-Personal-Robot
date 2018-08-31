package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Rotation2d;

import java.util.List;

public class Communication extends Subsystem {

    private static PeriodicIO mPeriodicIO;
    private double[] mResetPoseInput;
    private double[][] mTrajectory;

    public Communication(){
        mPeriodicIO = new PeriodicIO();
    }

    public void readPeriodicInputs() {
        //pose reset
        mPeriodicIO.wantPoseReset = SmartDashboard.getBoolean("wantPoseReset", false);
        mResetPoseInput = SmartDashboard.getNumberArray("wantedResetPose", new double[] {0,0,0});
        mPeriodicIO.wantedResetPose = new Pose2d(mResetPoseInput[0], mResetPoseInput[1], Rotation2d.fromDegrees(mResetPoseInput[2]));

        //trajectory
        mPeriodicIO.wantToRunTrajectory = SmartDashboard.getBoolean("wantToRunTrajectory", false);
        mPeriodicIO.wantToRecieveTrajectory = SmartDashboard.getBoolean("wantToReceiveTrajectory", false);

    }

    public void writePeriodicOutputs(){
        //TODO publish misc sensor data
    }

    public void outputTelemetry() {
        //no-op
    }

    @Override
    public void stop() {
        //no-op
    }

    @Override
    public void reset() {
        //no-op
    }

    public class PeriodicIO{
        public boolean wantPoseReset;
        public Pose2d wantedResetPose;

        public boolean wantToRunTrajectory;
        public boolean wantToRecieveTrajectory;
        public List<Pose2d> wantedTrajectory;

        //public

    }

}
