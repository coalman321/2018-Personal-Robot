package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.lib.AutoTrajectory.*;
import frc.lib.util.DataRecorder;
import frc.lib.util.HIDHelper;
import frc.robot.lib.loops.Loop;

import java.util.Set;

public class Drive extends Subsystem {

    //used internally for data
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private DriveControlState driveControlState = DriveControlState.OPEN_LOOP;
    private PurePursuitController pathFollowingController;
    private DataRecorder<PeriodicIO> dataRecorder;
    private PeriodicIO periodicIOInstance;

    //construct one and only 1 instance of this class
    private static final Drive m_DriveInstance = new Drive();

    public static Drive getInstance(){
        return m_DriveInstance;
    }

    private Drive() {
        periodicIOInstance = new PeriodicIO();
        dataRecorder = new DataRecorder<PeriodicIO>("", PeriodicIO.class);
        reset();
    }


    private final Loop mLoop = new Loop(){

        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this){

            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                if (Constants.ENABLE_MP_TEST_MODE) driveControlState = DriveControlState.PROFILING_TEST;
                switch (driveControlState) {
                    case PATH_FOLLOWING_CONTROL:
                        updatePathFollower();
                        if (isFinishedPath()) stop();
                        break;

                    case PROFILING_TEST:
                        if (DriverStation.getInstance().isAutonomous()) {
                            //driveTank(Constants.MP_TEST_SPEED, Constants.MP_TEST_SPEED);
                        }
                        break;

                    default: //open loop
                        if (DriverStation.getInstance().isOperatorControl()) operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        else operatorInput = new double[]{0, 0, 0};
                        break;
                }


            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    };

    private Runnable periodic = () -> {

    };

    public double getGyro() {
        //TODO implement value retrieval
        return 0;
        //return ((RobotMap.ahrs.getYaw() + 360) % 360); //add 360 to make all positive then mod by 360 to get remainder
    }

    public double getLeftEncoder(){
        //TODO implement value retrieval
        return 0;
    }

    public double getLeftVelocity(){
        //TODO implement value retrieval
        return 0;
    }

    public double getRightEncoder(){
        //TODO implement value retrieval
        return 0;
    }

    public double getRightVelocity(){
        //TODO implement value retrieval
        return 0;
    }

    public synchronized void followPath(Path path, boolean reversed) {
        pathFollowingController = new AdaptivePurePursuitController(Constants.PATH_FOLLOWING_LOOKAHEAD,
                Constants.PATH_FOLLOWING_MAX_ACCELERATION, Constants.LOOPER_DT, path, reversed, 1);
        driveControlState = DriveControlState.PATH_FOLLOWING_CONTROL;
        updatePathFollower();
    }

    public synchronized Set<String> getPathMarkersCrossed() {
        if (pathFollowingController == null) {
            return null;
        } else {
            return pathFollowingController.getMarkersCrossed();
        }
    }

    public void stop(){
        driveControlState = DriveControlState.OPEN_LOOP;
    }

    public synchronized boolean isFinishedPath() {
        return (driveControlState == DriveControlState.PATH_FOLLOWING_CONTROL && pathFollowingController.isDone())
                || driveControlState != DriveControlState.PATH_FOLLOWING_CONTROL;
    }

    public boolean isOpenLoop(){
        return driveControlState == DriveControlState.OPEN_LOOP;
    }

    public void setOperatorInput(double[] input){
        operatorInput = input;
    }

    public void reset(){
        //TODO add reset with sensor impl

    }


    private synchronized void updatePathFollower() {
        RigidTransform2d robot_pose = robotPose.getLatestFieldToVehicle().getValue();
        RigidTransform2d.Delta command = pathFollowingController.update(robot_pose, Timer.getFPGATimestamp());
        Kinematics.DriveVelocity setpoint = Kinematics.inverseKinematics(command);

        // Scale the command to respect the max velocity limits
        double max_vel = 0.0;
        max_vel = Math.max(max_vel, Math.abs(setpoint.left));
        max_vel = Math.max(max_vel, Math.abs(setpoint.right));
        if (max_vel > Constants.PATH_FOLLOWING_MAX_VELOCITY) {
            double scaling = Constants.PATH_FOLLOWING_MAX_VELOCITY / max_vel;
            setpoint = new Kinematics.DriveVelocity(setpoint.left * scaling, setpoint.right * scaling);
        }
        //driveTank(Util.inchesPerSecondToRpm(setpoint.left), Util.inchesPerSecondToRpm(setpoint.right));
    }

    public void outputTelemetry() {

        if (dataRecorder != null) {
            dataRecorder.write();
        }
    }

    public static double inchesToRotations(double inches) {
        return inches / (Constants.WHEEL_DIAMETER * Math.PI);
    }

    public static double inchesPerSecondToRpm(double inches_per_second) { return inchesToRotations(inches_per_second) * 60; }

    public static double uPer100MsToRPM(double uPer100Ms){
        return (uPer100Ms* 75) / 512.0;
    }

    public static double RPMToUnitsPer100Ms(double RPM){
        return (RPM * 512) / 75.0;
    }

    enum DriveControlState {
        OPEN_LOOP, PATH_FOLLOWING_CONTROL, PROFILING_TEST;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static class PeriodicIO{
        //inputs
        public Rotation2d gyroHeading;
        public int rearEncoderCount;
        public int steeringEncoderAngle;


        //outputs
        public double wantedVelocity;
        public Rotation2d wantedHeading;


    }

}
