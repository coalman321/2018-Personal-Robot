package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.*;

import java.util.Set;

public class RobotDriveV4 extends Subsystem {

    enum DriveControlState {
        OPEN_LOOP("Open Loop"), PATH_FOLLOWING_CONTROL("Path following"), PROFILING_TEST("Profiling test");
        private String s;

        DriveControlState(String name){
            s = name;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    //used internally for data
    private Notifier m_NotifierInstance;
    private boolean isReversed = false;
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private DriveControlState driveControlState = DriveControlState.OPEN_LOOP;
    private AdaptivePurePursuitController pathFollowingController;

    public RobotDriveV4() {

        m_NotifierInstance = new Notifier(periodic);
        reset();
        initGyro();
        startPeriodic();
    }

    private void startPeriodic(){
        m_NotifierInstance.startPeriodic(.DRIVETRAIN_UPDATE_RATE);
    }

    private Runnable periodic = () -> {
        synchronized (RobotDriveV4.this){
            if(Constants.ENABLE_MP_TEST_MODE) driveControlState = DriveControlState.PROFILING_TEST;
            if(DriverStation.getInstance().isEnabled()){
                switch (driveControlState){
                    case PATH_FOLLOWING_CONTROL:
                        updatePathFollower();
                        if (isFinishedPath()) stop();
                        break;

                    case PROFILING_TEST:
                        if(DriverStation.getInstance().isAutonomous()) {
                            driveTank(Constants.MP_TEST_SPEED, Constants.MP_TEST_SPEED);
                        }
                        break;

                    default: //open loop
                        if(DriverStation.getInstance().isOperatorControl())operatorInput = getAdjStick();
                        else operatorInput = new double[] {0,0,0};
                        if (isReversed) {
                            operatorInput[0] *= -1;
                            operatorInput[1] *= -1;
                        }
                        if (Robot.oi.getMasterStick().getPOV() >= 0) {
                            operatorInput[0] *= Constants.getTeleopYCutPercentage();
                            operatorInput[1] *= Constants.getTeleopXCutPercentage();
                        }
                        if (enLock) operatorInput[2] = pidOutput;
                        else setTarget(getGyro()); // Safety feature in case PID gets enabled
                        driveCartesian(operatorInput[1], -operatorInput[0], operatorInput[2]);
                        break;
                }
            }
            smartDashboardUpdates();
        }
    };

    public double getGyro() {
        return ((RobotMap.ahrs.getYaw() + 360) % 360); //add 360 to make all positive then mod by 360 to get remainder
    }

    public double getGyroContinuous(){
        return RobotMap.ahrs.getAngle();
    }

    public double getLeftEncoder(){
        return -RobotMap.driveFrontLeft.getSensorCollection().getQuadraturePosition();
    }

    public double getLeftVelocity(){
        return -RobotMap.driveFrontLeft.getSensorCollection().getQuadratureVelocity();
    }

    public double getRightEncoder(){
        return RobotMap.driveFrontRight.getSensorCollection().getQuadraturePosition();
    }

    public double getRightVelocity(){
        return RobotMap.driveFrontRight.getSensorCollection().getQuadratureVelocity();
    }

    public synchronized void followPath(Path path, boolean reversed) {
        pathFollowingController = new AdaptivePurePursuitController(Constants.PATH_FOLLOWING_LOOKAHEAD,
                Constants.PATH_FOLLOWING_MAX_ACCELERATION, Constants.DRIVETRAIN_UPDATE_RATE, path, reversed, 1);
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
        driveTank(-Math.signum(lastval) * Constants.BRAKE_RPM, -Math.signum(lastval) * Constants.BRAKE_RPM);
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

    public void setIsReversed(boolean isReversed){
        this.isReversed = isReversed;
    }

    public void reset(){
        m_MixedDriveInstance.resetSelectedSensors();
        resetGyro();
    }


    private void resetGyro(){
        RobotMap.ahrs.reset();
    }

    private static double inchesToRotations(double inches) {
        return inches / (Constants.WHEEL_DIAMETER * Math.PI);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double uPer100MsToRPM(double uPer100Ms){
        return (uPer100Ms* 75) / 512.0;
    }

    private static double RPMToUnitsPer100Ms(double RPM){
        return (RPM * 512) / 75.0;
    }

    private synchronized void updatePathFollower() {
        RigidTransform2d robot_pose = RobotMap.robotPose.getLatestFieldToVehicle().getValue();
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
        driveTank(inchesPerSecondToRpm(setpoint.left), inchesPerSecondToRpm(setpoint.right));
    }

    private void initGyro(){
        gyroLock = new PIDController(Constants.getGyrolockKp(), Constants.getGyrolockKi(), Constants.getGyrolockKd(), this, this);
        gyroLock.setAbsoluteTolerance(Constants.getGyrolockTol());
        gyroLock.setOutputRange(-Constants.getGyrolockLim(), Constants.getGyrolockLim());
        gyroLock.setInputRange(0, 360);
        gyroLock.setContinuous();
    }

    private double[] getAdjStick() {
        double[] out = new double[3];
        out[0] = evalDeadBand(Robot.oi.getMasterStick().getY(), Constants.getTeleopDeadband()) * Constants.getTeleopYPercentage();
        out[1] = evalDeadBand(Robot.oi.getMasterStick().getX(), Constants.getTeleopDeadband()) * Constants.getTeleopXPercentage();
        out[2] = evalDeadBand(Robot.oi.getMasterStick().getZ(), Constants.getTeleopDeadband()) * Constants.getTeleopZPercentage();
        return out;
    }

    // figures out if the stick value is within the deadband
    private double evalDeadBand(double stickInpt, double deadBand) {
        if (Math.abs(stickInpt) < deadBand) {
            return 0;
        } else {
            if (stickInpt < 0) {
                return (0 - Math.pow(stickInpt, 2));
            } else {
                return Math.pow(stickInpt, 2);
            }
        }
    }

}
