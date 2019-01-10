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
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Pose2dWithCurvature;
import frc.lib.geometry.Rotation2d;
import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.lib.trajectory.TrajectoryIterator;
import frc.lib.trajectory.timing.TimedState;
import frc.lib.util.DriveHelper;
import frc.lib.util.DriveSignal;
import frc.lib.util.HIDHelper;
import frc.lib.util.Units;
import frc.robot.Constants;
import frc.robot.motionplanners.DriveMotionPlanner;

public class Drive extends Subsystem {

    private static final Drive M_DRIVE = new Drive();

    //system variables
    private DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private Rotation2d gyroOffset = Rotation2d.fromDegrees(0);
    private DriveMotionPlanner motionPlanner;
    private PeriodicIO periodicIO;
    private boolean overrideTrajectory = false;

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
                            setVelocity(new DriveSignal(Units.RPMToUnitsPer100Ms(Units.inchesPerSecondToRpm(Constants.MP_TEST_SPEED)),
                                    Units.RPMToUnitsPer100Ms(Units.inchesPerSecondToRpm(Constants.MP_TEST_SPEED))));
                        }
                        break;

                    case OPEN_LOOP:
                        if (DriverStation.getInstance().isOperatorControl())
                            operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        else operatorInput = new double[]{0, 0, 0};
                        setOpenLoop(DriveHelper.arcadeDrive(operatorInput[1], operatorInput[2], false));
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
        motionPlanner = new DriveMotionPlanner();
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

    public double getLeftEncoderDistance() {
        return Units.rotationsToInches(getLeftEncoderRotations());
    }

    public double getRightEncoderDistance() {
        return Units.rotationsToInches(getRightEncoderRotations());
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

    /**
     * Configure talons for open loop control
     */
    public synchronized void setOpenLoop(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.OPEN_LOOP) {
            System.out.println("Switching to open loop");

            mDriveControlState = DriveControlState.OPEN_LOOP;
        }
        periodicIO.left_demand = -signal.getLeft();
        periodicIO.right_demand = -signal.getRight();
    }

    /**
     * Configures talons for velocity control
     */
    public synchronized void setVelocity(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.PATH_FOLLOWING) {
            System.out.println("Switching to velocity control");

            mDriveControlState = DriveControlState.PATH_FOLLOWING;
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
        periodicIO.left_demand = -signal.getLeft();
        periodicIO.right_demand = -signal.getRight();
    }

    public boolean isDoneWithTrajectory() {
        if (motionPlanner == null || mDriveControlState != DriveControlState.PATH_FOLLOWING) {
            return false;
        }
        return motionPlanner.isDone() || overrideTrajectory;
    }

    public synchronized void setTrajectory(TrajectoryIterator<TimedState<Pose2dWithCurvature>> trajectory) {
        if (motionPlanner != null) {
            overrideTrajectory = false;
            motionPlanner.reset();
            motionPlanner.setTrajectory(trajectory);
            mDriveControlState = DriveControlState.PATH_FOLLOWING;
        }
    }

    public void overrideTrajectory(boolean value) {
        overrideTrajectory = value;
    }

    private void updatePathFollower() {
        if (mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            final double now = Timer.getFPGATimestamp();

            DriveMotionPlanner.Output output = motionPlanner.update(now, PoseEstimator.getInstance().getFieldToVehicle(now));

            periodicIO.error = motionPlanner.error();
            periodicIO.path_setpoint = motionPlanner.setpoint();

            if (!overrideTrajectory) {
                setVelocity(new DriveSignal(Units.radiansPerSecondToTicksPer100ms(output.left_velocity),
                        Units.radiansPerSecondToTicksPer100ms(output.right_velocity)));
            } else {
                setVelocity(DriveSignal.BRAKE);
            }
        } else {
            DriverStation.reportError("Drive is not in path following state", false);
        }
    }

    private void configTalons() {
        frontLeft.setNeutralMode(NeutralMode.Coast);
        frontLeft.setInverted(false);
        frontLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.DRIVE_LEFT_PID_IDX, 0);
        frontLeft.setSensorPhase(true);
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
        frontRight.setSensorPhase(true);
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
        SmartDashboard.putNumber("drive/leftRPM", Units.uPer100MsToRPM(periodicIO.left_velocity_ticks_per_100ms));
        SmartDashboard.putNumber("drive/rightRPM", Units.uPer100MsToRPM(periodicIO.right_velocity_ticks_per_100ms));
        SmartDashboard.putNumber("drive/leftPosition", periodicIO.left_position_ticks);
        SmartDashboard.putNumber("drive/rightPosition", periodicIO.right_position_ticks);
        SmartDashboard.putNumber("drive/gyro", periodicIO.gyro_heading.getDegrees());
        SmartDashboard.putNumberArray("drive/operatorInput", operatorInput);
        SmartDashboard.putString("drive/controlMode", mDriveControlState.toString());
    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {
        mDriveControlState = DriveControlState.OPEN_LOOP;
        periodicIO = new PeriodicIO();
        motionPlanner.reset();
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
        public Pose2d error = Pose2d.identity();
        public TimedState<Pose2dWithCurvature> path_setpoint = new TimedState<Pose2dWithCurvature>(Pose2dWithCurvature.identity());

        // OUTPUTS
        public double left_demand;
        public double right_demand;
    }
}