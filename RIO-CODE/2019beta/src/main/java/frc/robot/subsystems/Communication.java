package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Rotation2d;
import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;

import java.util.ArrayList;


public class Communication extends Subsystem {

    private static PeriodicIO mPeriodicIO;
    private double[] mResetPoseInput;
    private double[] mTrajectory;
    private static final Communication mInstance = new Communication();

    public static Communication getInstance() {
        return mInstance;
    }

    private Communication(){
        mPeriodicIO = new PeriodicIO();
    }

    public void readPeriodicInputs() {
        //pose reset
        mPeriodicIO.wantPoseReset = SmartDashboard.getBoolean("wantPoseReset", false);
        if(mPeriodicIO.wantPoseReset){
            mResetPoseInput = SmartDashboard.getNumberArray("wantedResetPose", new double[] {0,0,0});
            mPeriodicIO.wantedResetPose = new Pose2d(mResetPoseInput[0], mResetPoseInput[1], Rotation2d.fromDegrees(mResetPoseInput[2]));
        }

        //trajectory
        mPeriodicIO.wantToRunTrajectory = SmartDashboard.getBoolean("wantToRunTrajectory", false);
        mPeriodicIO.wantToRecieveTrajectory = SmartDashboard.getBoolean("wantToReceiveTrajectory", false);
        if(mPeriodicIO.wantToRecieveTrajectory) {
            mTrajectory = SmartDashboard.getNumberArray("wantedTrajectory", new double[] {});
            ArrayList<Pose2d> local_trajectory = new ArrayList<>();
            for(int i = 0; i < mTrajectory.length / 3; i +=3){
                // i = x, i+1 = y, i+2 = r
                local_trajectory.add(new Pose2d(mTrajectory[i], mTrajectory[i+1], Rotation2d.fromDegrees(mTrajectory[i+2])));
            }
            mPeriodicIO.wantedTrajectory = local_trajectory;
        }

    }

    public void writePeriodicOutputs(){
        //TODO publish misc sensor data
    }

    public PeriodicIO getPeriodicIO() {
        return mPeriodicIO;
    }

    public void outputTelemetry() {
        //no-op
    }

    public void stop() {
        //no-op
    }

    public void reset() {
        //no-op
    }

    public static class PeriodicIO{
        //Pose
        public boolean wantPoseReset;
        public Pose2d wantedResetPose;

        //Trajectory
        public boolean wantToRunTrajectory;
        public boolean wantToRecieveTrajectory;
        public ArrayList<Pose2d> wantedTrajectory;

        //?
        //public

    }

}
