package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.AutoTrajectory.*;
import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.lib.util.DriveHelper;
import frc.lib.util.DriveSignal;
import frc.lib.util.HIDHelper;
import frc.robot.Constants;

import java.util.Set;

public class Drive extends Subsystem {

    private static final Drive M_DRIVE = new Drive();

    //system variables
    private DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private AdaptivePurePursuitController pathFollowingController;
    private Rotation2d gyroOffset = Rotation2d.fromDegrees(0);
    private PeriodicIO periodicIO;

    //IO unit declarations
    private TalonSRX frontLeft, frontRight, rearLeft, rearRight;
    private PigeonIMU imu;

    private final Loop mLoop = new Loop() {

        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this) {

            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                if (Constants.ENABLE_MP_TEST_MODE && DriverStation.getInstance().isTest())
                    mDriveControlState = DriveControlState.PROFILING_TEST;
                switch (mDriveControlState) {
                    case PATH_FOLLOWING:
                        updatePathFollower();
                        break;
                    case PROFILING_TEST:
                        if (DriverStation.getInstance().isTest()) {
                            drive(new DriveSignal(RPMToUnitsPer100Ms(inchesPerSecondToRpm(Constants.MP_TEST_SPEED)), RPMToUnitsPer100Ms(inchesPerSecondToRpm(Constants.MP_TEST_SPEED))));
                        }
                        break;

                    case OPEN_LOOP:
                        if (DriverStation.getInstance().isOperatorControl())
                            operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        else operatorInput = new double[]{0, 0, 0};
                        drive(DriveHelper.arcadeDrive(operatorInput[1], operatorInput[2], false));
                        break;
                }
                outputTelemetry();
            }
        }

        @Override
        public void onStop(double timestamp) {
            synchronized (Drive.this) {

            }
        }
    };

    private Drive() {
        periodicIO = new PeriodicIO();
        frontLeft = new TalonSRX(Constants.DRIVE_FRONT_LEFT_ID);
        frontRight = new TalonSRX(Constants.DRIVE_FRONT_RIGHT_ID);
        rearLeft = new TalonSRX(Constants.DRIVE_REAR_LEFT_ID);
        rearRight = new TalonSRX(Constants.DRIVE_REAR_RIGHT_ID);
        imu = new PigeonIMU(Constants.PIGEON_ID);
        configTalons();
    }

    public static Drive getInstance() {
        return M_DRIVE;
    }

    private static double inchesToRotations(double inches) {
        return inches / (Constants.WHEEL_DIAMETER * Math.PI);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double uPer100MsToRPM(double uPer100Ms) {
        return (uPer100Ms * 75) / 512.0;
    }

    private static double RPMToUnitsPer100Ms(double RPM) {
        return (RPM * 512) / 75.0;
    }

    public synchronized void followPath(Path path, boolean reversed) {
        pathFollowingController = new AdaptivePurePursuitController(Constants.PATH_FOLLOWING_LOOKAHEAD,
                Constants.PATH_FOLLOWING_MAX_ACCELERATION, Constants.LOOPER_DT, path, reversed, 1);
        mDriveControlState = DriveControlState.PATH_FOLLOWING;
        updatePathFollower();
    }

    public synchronized Set<String> getPathMarkersCrossed() {
        if (pathFollowingController == null) {
            return null;
        } else {
            return pathFollowingController.getMarkersCrossed();
        }
    }

    public synchronized boolean isFinishedPath() {
        if (mDriveControlState == DriveControlState.PATH_FOLLOWING && pathFollowingController.isDone()) {
            mDriveControlState = DriveControlState.OPEN_LOOP;
            return true;
        } else if (mDriveControlState != DriveControlState.PATH_FOLLOWING) return true;
        return false;
    }

    private synchronized void updatePathFollower() {

    }

    private void drive(DriveSignal signal) {
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
    }

    public synchronized Rotation2d getHeading() {
        return periodicIO.gyro_heading;
    }

    public synchronized void setHeading(Rotation2d heading) {
        System.out.println("SET HEADING: " + heading.getDegrees());

        gyroOffset = heading.rotateBy(Rotation2d.fromDegrees(imu.getFusedHeading()).inverse());
        System.out.println("Gyro offset: " + gyroOffset.getDegrees());

        periodicIO.gyro_heading = heading;
    }

    public double getLeftEncoderRotations() {
        return periodicIO.left_position_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public double getRightEncoderRotations() {
        return periodicIO.right_position_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public void resetEncoders() {
        frontLeft.setSelectedSensorPosition(0, Constants.DRIVE_LEFT_PID_IDX, 0);
        frontRight.setSelectedSensorPosition(0, Constants.DRIVE_RIGHT_PID_IDX, 0);
    }

    public synchronized void readPeriodicInputs() {
        periodicIO.gyro_heading = Rotation2d.fromDegrees(imu.getFusedHeading()).rotateBy(gyroOffset);
        periodicIO.left_position_ticks = frontLeft.getSelectedSensorPosition(0);
        periodicIO.right_position_ticks = frontRight.getSelectedSensorPosition(0);
        periodicIO.left_velocity_ticks_per_100ms = frontLeft.getSelectedSensorVelocity(0);
        periodicIO.right_velocity_ticks_per_100ms = frontRight.getSelectedSensorVelocity(0);
    }

    public synchronized void writePeriodicOutputs() {
        if (mDriveControlState == DriveControlState.PATH_FOLLOWING || mDriveControlState == DriveControlState.PROFILING_TEST) {
            frontRight.set(ControlMode.Velocity, periodicIO.right_demand);
            rearRight.set(ControlMode.Follower, frontRight.getDeviceID());
            frontLeft.set(ControlMode.Velocity, periodicIO.left_demand);
            rearLeft.set(ControlMode.Follower, frontLeft.getDeviceID());
        } else {
            frontRight.set(ControlMode.PercentOutput, periodicIO.right_demand);
            rearRight.set(ControlMode.Follower, frontRight.getDeviceID());
            frontLeft.set(ControlMode.PercentOutput, periodicIO.left_demand);
            rearLeft.set(ControlMode.Follower, frontLeft.getDeviceID());
        }
    }

    private void configTalons() {
        frontLeft.setNeutralMode(NeutralMode.Coast);
        frontLeft.setInverted(false);
        frontLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.DRIVE_LEFT_PID_IDX, 0);
        frontLeft.setSensorPhase(true); //TODO validate via webdash
        frontLeft.selectProfileSlot(0, Constants.DRIVE_LEFT_PID_IDX); //keep slotidx the same
        frontLeft.config_kF(0, Constants.DRIVE_LEFT_KF, 0);
        frontLeft.config_kP(0, Constants.DRIVE_LEFT_KP, 0);
        frontLeft.config_kI(0, Constants.DRIVE_LEFT_KI, 0);
        frontLeft.config_kD(0, Constants.DRIVE_LEFT_KD, 0);
        frontLeft.config_IntegralZone(0, 0, 0);
        frontLeft.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature,10,0);

        frontRight.setNeutralMode(NeutralMode.Coast);
        frontRight.setInverted(true);
        frontRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.DRIVE_RIGHT_PID_IDX, 0);
        frontRight.setSensorPhase(true); //TODO validate via webdash
        frontRight.selectProfileSlot(0, Constants.DRIVE_RIGHT_PID_IDX); //keep slotidx the same
        frontRight.config_kF(0, Constants.DRIVE_RIGHT_KF, 0);
        frontRight.config_kP(0, Constants.DRIVE_RIGHT_KP, 0);
        frontRight.config_kI(0, Constants.DRIVE_RIGHT_KI, 0);
        frontRight.config_kD(0, Constants.DRIVE_RIGHT_KD, 0);
        frontRight.config_IntegralZone(0, 0, 0);
        frontRight.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature,10,0);

        rearRight.setNeutralMode(NeutralMode.Coast);
        rearRight.setInverted(true);

        rearLeft.setNeutralMode(NeutralMode.Coast);
        rearLeft.setInverted(false);

        resetEncoders();
    }

    @Override
    public void outputTelemetry() {
        SmartDashboard.putNumber("drive/leftDemand", periodicIO.left_demand);
        SmartDashboard.putNumber("drive/rightDemand", periodicIO.right_demand);
        SmartDashboard.putNumber("drive/leftVelocity", periodicIO.left_velocity_ticks_per_100ms);
        SmartDashboard.putNumber("drive/rightVelocity", periodicIO.right_velocity_ticks_per_100ms);
        SmartDashboard.putNumber("drive/leftRPM", uPer100MsToRPM(periodicIO.left_velocity_ticks_per_100ms));
        SmartDashboard.putNumber("drive/rightRPM", uPer100MsToRPM(periodicIO.right_velocity_ticks_per_100ms));
        SmartDashboard.putNumber("drive/leftPosition", periodicIO.left_position_ticks);
        SmartDashboard.putNumber("drive/rightPosition", periodicIO.right_position_ticks);
        SmartDashboard.putNumber("drive/gyro", periodicIO.gyro_heading.getDegrees());
        SmartDashboard.putNumberArray("drive/operatorInput", operatorInput);
        SmartDashboard.putString("drive/controlMode", mDriveControlState.toString());
        if (pathFollowingController != null)
            SmartDashboard.putString("drive/markersPassed", pathFollowingController.getMarkersCrossed().toString());
        //else SmartDashboard.putString("drive/ Markers passed", "");
    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {
        mDriveControlState = DriveControlState.OPEN_LOOP;
        periodicIO = new PeriodicIO();
        setHeading(Rotation2d.identity());
        resetEncoders();
    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(mLoop);
    }

    enum DriveControlState {
        OPEN_LOOP,
        PATH_FOLLOWING,
        PROFILING_TEST;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static class PeriodicIO {
        // INPUTS
        public int left_position_ticks;
        public int right_position_ticks;
        public int left_velocity_ticks_per_100ms;
        public int right_velocity_ticks_per_100ms;
        public Rotation2d gyro_heading = Rotation2d.fromDegrees(0);

        // OUTPUTS
        public double left_demand;
        public double right_demand;
    }
}