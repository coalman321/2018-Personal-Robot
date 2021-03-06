package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
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
import frc.lib.util.DriveSignal;
import frc.lib.util.HIDHelper;
import frc.robot.Constants;
import frc.robot.planners.DriveMotionPlanner;

public class Drive extends Subsystem {

    //construct one and only 1 instance of this class
    private static Drive m_DriveInstance = new Drive();

    public static Drive getInstance() {
        return m_DriveInstance;
    }

    //used internally for data
    private DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    private DriveMotionPlanner mMotionPlanner;
    private boolean mOverrideTrajectory = false;
    private DriveIO periodicIO;
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private PigeonIMU pigeonIMU;
    private TalonSRX driveFrontLeft, driveRearLeft, driveFrontRight, driveRearRight;

    private final Loop mLoop = new Loop() {

        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this) {
            }

        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                if (Constants.ENABLE_MP_TEST_MODE && DriverStation.getInstance().isTest()) {
                    mDriveControlState = DriveControlState.PROFILING_TEST;
                }

                switch (mDriveControlState) {
                    case PATH_FOLLOWING:
                        updatePathFollower();
                        break;

                    case PROFILING_TEST:
                        if (Constants.RAMPUP) {
                            periodicIO.left_demand = -(periodicIO.ramp_Up_Counter * .0025 + .01);
                            periodicIO.right_demand = -(periodicIO.ramp_Up_Counter * .0025 + .01);
                            periodicIO.ramp_Up_Counter++;
                        } else if (DriverStation.getInstance().isTest()) {
                            periodicIO.left_demand = radiansPerSecondToTicksPer100ms(inchesPerSecondToRadiansPerSecond(Constants.MP_TEST_SPEED));
                            periodicIO.right_demand = radiansPerSecondToTicksPer100ms(inchesPerSecondToRadiansPerSecond(Constants.MP_TEST_SPEED));
                        }

                        break;

                    //TODO Change z input to PID
                    case GYRO_LOCK:
                        /*operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        SmartDashboard.putNumberArray("stick", operatorInput);
                        operatorInput[2] = (periodicIO.gyro_heading.getDegrees() - periodicIO.gyro_pid_angle) / 720;
                        setOpenLoop(arcadeDrive(operatorInput[1], operatorInput[2]));
                        break;*/

                    case OPEN_LOOP:
                        operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        SmartDashboard.putNumberArray("stick", operatorInput);
                        setOpenLoop(arcadeDrive(operatorInput[1], operatorInput[2]));
                        break;

                    case ANGLE_PID:
                        // Let the AnglePIDOutput set the values
                        break;

                    default:
                        System.out.println("Unexpected control state");
                }


            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    };

    @Override
    public synchronized void readPeriodicInputs() {
        double prevLeftTicks = periodicIO.left_pos_ticks;
        double prevRightTicks = periodicIO.right_pos_ticks;

        periodicIO.left_pos_ticks = -driveFrontLeft.getSelectedSensorPosition(0);
        periodicIO.right_pos_ticks = -driveFrontRight.getSelectedSensorPosition(0);
        periodicIO.left_velocity_ticks_per_100ms = -driveFrontLeft.getSelectedSensorVelocity(0);
        periodicIO.right_velocity_ticks_per_100ms = -driveFrontRight.getSelectedSensorVelocity(0);
        periodicIO.gyro_heading = Rotation2d.fromDegrees(pigeonIMU.getFusedHeading()).rotateBy(periodicIO.gyro_offset);
        periodicIO.left_error = driveFrontLeft.getClosedLoopError();
        periodicIO.right_error = driveFrontRight.getClosedLoopError();

        double deltaLeftTicks = ((periodicIO.left_pos_ticks - prevLeftTicks) / 4096.0) * Math.PI;
        periodicIO.left_distance += deltaLeftTicks * Constants.DRIVE_WHEEL_DIAMETER_INCHES;
        double deltaRightTicks = ((periodicIO.right_pos_ticks - prevRightTicks) / 4096.0) * Math.PI;
        periodicIO.right_distance += deltaRightTicks * Constants.DRIVE_WHEEL_DIAMETER_INCHES;

    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if (mDriveControlState == DriveControlState.OPEN_LOOP || mDriveControlState == DriveControlState.ANGLE_PID || (mDriveControlState == DriveControlState.PROFILING_TEST && Constants.RAMPUP)) {
            driveFrontLeft.set(ControlMode.PercentOutput, periodicIO.left_demand);
            driveFrontRight.set(ControlMode.PercentOutput, periodicIO.right_demand);

        } else {
            driveFrontLeft.set(ControlMode.Velocity, -periodicIO.left_demand, DemandType.ArbitraryFeedForward, -(periodicIO.left_feedforward + Constants.DRIVE_LEFT_KD * periodicIO.left_accl / 1023.0));
            driveFrontRight.set(ControlMode.Velocity, -periodicIO.right_demand, DemandType.ArbitraryFeedForward, -(periodicIO.right_feedforward + Constants.DRIVE_RIGHT_KD * periodicIO.right_accl / 1023.0));
        }
    }

    private Drive() {
        mMotionPlanner = new DriveMotionPlanner();
        driveFrontLeft = new TalonSRX(Constants.DRIVE_FRONT_LEFT_ID);
        driveRearLeft = new TalonSRX(Constants.DRIVE_BACK_LEFT_ID);
        driveFrontRight = new TalonSRX(Constants.DRIVE_FRONT_RIGHT_ID);
        driveRearRight = new TalonSRX(Constants.DRIVE_BACK_RIGHT_ID);
        pigeonIMU = new PigeonIMU(Constants.PIGEON_IMU_ID);
        configTalons();
        reset();

    }

    public synchronized Rotation2d getHeading() {
        return periodicIO.gyro_heading;
    }

    public synchronized void setHeading(Rotation2d heading) {
        System.out.println("SET HEADING: " + heading.getDegrees());
        periodicIO.gyro_offset = heading.rotateBy(Rotation2d.fromDegrees(pigeonIMU.getFusedHeading()).inverse());
        System.out.println("Gyro offset: " + periodicIO.gyro_offset.getDegrees());
        periodicIO.gyro_heading = heading;
    }

    public double getLeftEncoderRotations() {
        return periodicIO.left_pos_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public double getRightEncoderRotations() {
        return periodicIO.right_pos_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public double getLeftEncoderDistance() {
        return rotationsToInches(getLeftEncoderRotations());
    }

    public double getRightEncoderDistance() {
        return rotationsToInches(getRightEncoderRotations());
    }

    public double getLeftVelocityNativeUnits() {
        return periodicIO.left_velocity_ticks_per_100ms;
    }

    public double getRightVelocityNativeUnits() {
        return periodicIO.right_velocity_ticks_per_100ms;
    }

    public double getLeftLinearVelocity() {
        return rotationsToInches(getLeftVelocityNativeUnits() * 10.0 / Constants.DRIVE_ENCODER_PPR);
    }

    public double getRightLinearVelocity() {
        return rotationsToInches(getRightVelocityNativeUnits() * 10.0 / Constants.DRIVE_ENCODER_PPR);
    }

    public void reset() {
        mOverrideTrajectory = false;
        mMotionPlanner.reset();
        mMotionPlanner.setFollowerType(DriveMotionPlanner.FollowerType.NONLINEAR_FEEDBACK);
        periodicIO = new DriveIO();
        setHeading(Rotation2d.fromDegrees(0));
        resetEncoders();

    }

    private void resetEncoders() {
        driveFrontRight.setSelectedSensorPosition(0, 0, 0);
        driveFrontLeft.setSelectedSensorPosition(0, 0, 0);
    }

    private void configTalons() {
        driveFrontLeft.setSensorPhase(true);
        driveFrontLeft.selectProfileSlot(0, 0);
        driveFrontLeft.config_kF(0, Constants.DRIVE_LEFT_KF, 0);
        driveFrontLeft.config_kP(0, Constants.DRIVE_LEFT_KP, 0);
        driveFrontLeft.config_kI(0, Constants.DRIVE_LEFT_KI, 0);
        driveFrontLeft.config_kD(0, Constants.DRIVE_LEFT_KD, 0);
        driveFrontLeft.config_IntegralZone(0, 300);
        driveFrontLeft.setInverted(false);
        driveFrontLeft.setNeutralMode(NeutralMode.Brake);
        driveFrontLeft.configVoltageCompSaturation(Constants.DRIVE_VCOMP);
        driveFrontLeft.enableVoltageCompensation(true);

        driveRearLeft.setInverted(false);
        driveRearLeft.setNeutralMode(NeutralMode.Brake);
        driveRearLeft.configVoltageCompSaturation(Constants.DRIVE_VCOMP);
        driveRearLeft.enableVoltageCompensation(true);
        driveRearLeft.follow(driveFrontLeft);


        driveFrontRight.setSensorPhase(true);
        driveFrontRight.selectProfileSlot(0, 0);
        driveFrontRight.config_kF(0, Constants.DRIVE_RIGHT_KF, 0);
        driveFrontRight.config_kP(0, Constants.DRIVE_RIGHT_KP, 0);
        driveFrontRight.config_kI(0, Constants.DRIVE_RIGHT_KI, 0);
        driveFrontRight.config_kD(0, Constants.DRIVE_RIGHT_KD, 0);
        driveFrontRight.config_IntegralZone(0, 300);
        driveFrontRight.setInverted(true);
        driveFrontRight.setNeutralMode(NeutralMode.Brake);
        driveFrontRight.configVoltageCompSaturation(Constants.DRIVE_VCOMP);
        driveFrontRight.enableVoltageCompensation(true);

        driveRearRight.setInverted(true);
        driveRearRight.setNeutralMode(NeutralMode.Brake);
        driveRearRight.configVoltageCompSaturation(Constants.DRIVE_VCOMP);
        driveRearRight.enableVoltageCompensation(true);
        driveRearRight.follow(driveFrontRight);

    }

    public void overrideTrajectory(boolean value) {
        mOverrideTrajectory = value;
    }

    private void updatePathFollower() {
        if (mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            final double now = Timer.getFPGATimestamp();

            DriveMotionPlanner.Output output = mMotionPlanner.update(now, PoseEstimator.getInstance().getFieldToVehicle(now));

            periodicIO.error = mMotionPlanner.error();
            periodicIO.path_setpoint = mMotionPlanner.setpoint();

            if (!mOverrideTrajectory) {
                DriveSignal signal = new DriveSignal(radiansPerSecondToTicksPer100ms(output.left_velocity),
                        radiansPerSecondToTicksPer100ms(output.right_velocity));

                setVelocity(signal, new DriveSignal(output.left_feedforward_voltage / 12, output.right_feedforward_voltage / 12));
                periodicIO.left_accl = radiansPerSecondToTicksPer100ms(output.left_accel) / 1000;
                periodicIO.right_accl = radiansPerSecondToTicksPer100ms(output.right_accel) / 1000;

            } else {
                setVelocity(DriveSignal.BRAKE, DriveSignal.BRAKE);
                mDriveControlState = DriveControlState.OPEN_LOOP;
                mMotionPlanner.reset();
            }
        } else {
            DriverStation.reportError("Drive is not in path following state", false);
        }
    }

    /**
     * Configure talons for open loop control
     *
     * @param signal input to drive train
     */
    public synchronized void setOpenLoop(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.OPEN_LOOP) {
            System.out.println("Switching to open loop");
            driveFrontLeft.set(ControlMode.PercentOutput, 0);
            driveFrontRight.set(ControlMode.PercentOutput, 0);
            mDriveControlState = DriveControlState.OPEN_LOOP;
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
    }

    public synchronized void setGyroLock(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.GYRO_LOCK) {
            System.out.println("Switching to open loop");
            driveFrontLeft.set(ControlMode.PercentOutput, 0);
            driveFrontRight.set(ControlMode.PercentOutput, 0);
            mDriveControlState = DriveControlState.GYRO_LOCK;
        }
        periodicIO.gyro_pid_angle = periodicIO.gyro_heading.getDegrees();
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
    }

    /**
     * Configure talons for Angle PID control
     */
    public synchronized void setAnglePidLoop(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.ANGLE_PID) {
            System.out.println("Switching to angle control");
            driveFrontLeft.set(ControlMode.PercentOutput, 0);
            driveFrontRight.set(ControlMode.PercentOutput, 0);
            mDriveControlState = DriveControlState.ANGLE_PID;
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
    }

    /**
     * Configures talons for velocity control
     */
    public synchronized void setVelocity(DriveSignal signal, DriveSignal feedforward) {
        if (mDriveControlState != DriveControlState.PATH_FOLLOWING) {
            System.out.println("Switching to velocity control");
            driveFrontLeft.set(ControlMode.Velocity, 0);
            driveFrontRight.set(ControlMode.Velocity, 0);
            mDriveControlState = DriveControlState.PATH_FOLLOWING;
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
        periodicIO.left_feedforward = feedforward.getLeft();
        periodicIO.right_feedforward = feedforward.getRight();


    }

    public synchronized void setTrajectory(TrajectoryIterator<TimedState<Pose2dWithCurvature>> trajectory) {
        if (mMotionPlanner != null) {
            mOverrideTrajectory = false;
            mMotionPlanner.reset();
            mMotionPlanner.setTrajectory(trajectory);
            mDriveControlState = DriveControlState.PATH_FOLLOWING;
        }
    }

    public boolean isDoneWithTrajectory() {
        if (mMotionPlanner == null || mDriveControlState != DriveControlState.PATH_FOLLOWING) {
            return true;
        }
        return mMotionPlanner.isDone() || mOverrideTrajectory;
    }

    public void outputTelemetry() {
        //literally breaks the purpose of the design pattern
        SmartDashboard.putString("Drive/Drive State", mDriveControlState.toString());

        SmartDashboard.putNumber("Drive/Error/X", periodicIO.error.getTranslation().x());
        SmartDashboard.putNumber("Drive/Error/Y", periodicIO.error.getTranslation().y());

        SmartDashboard.putNumber("Drive/Left/Demand", periodicIO.left_demand);
        SmartDashboard.putNumber("Drive/Left/Talon Velocity", periodicIO.left_velocity_ticks_per_100ms);
        SmartDashboard.putNumber("Drive/Left/Talon Error", periodicIO.left_error);
        SmartDashboard.putNumber("Drive/Left/Talon Voltage Out", driveFrontLeft.getMotorOutputVoltage());
        SmartDashboard.putNumber("Drive/Left/Encoder Counts", periodicIO.left_pos_ticks);

        SmartDashboard.putNumber("Drive/Right/Demand", periodicIO.right_demand);
        SmartDashboard.putNumber("Drive/Right/Talon Velocity", periodicIO.right_velocity_ticks_per_100ms);
        SmartDashboard.putNumber("Drive/Right/Talon Error", periodicIO.right_error);
        SmartDashboard.putNumber("Drive/Right/Talon Voltage Out", driveFrontRight.getMotorOutputVoltage());
        SmartDashboard.putNumber("Drive/Right/Encoder Counts", periodicIO.right_pos_ticks);

    }

    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(mLoop);
    }

    @Override
    public PeriodicIO getLogger() {
        return periodicIO;
    }

    enum DriveControlState {
        OPEN_LOOP,
        PATH_FOLLOWING,
        PROFILING_TEST,
        GYRO_LOCK,
        ANGLE_PID;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public class DriveIO extends PeriodicIO {
        // INPUTS
        public int left_pos_ticks;
        public int right_pos_ticks;
        public int left_velocity_ticks_per_100ms;
        public int right_velocity_ticks_per_100ms;
        public Rotation2d gyro_heading = Rotation2d.identity();
        public Rotation2d gyro_offset = Rotation2d.identity();
        public Pose2d error = Pose2d.identity();
        public TimedState<Pose2dWithCurvature> path_setpoint = new TimedState<>(Pose2dWithCurvature.identity());
        public double right_error = 0;
        public double left_error = 0;
        public double gyro_pid_angle = 0;


        // OUTPUTS
        public double ramp_Up_Counter = 0;
        public double left_accl = 0.0;
        public double left_demand = 0.0;
        public double left_distance = 0.0;
        public double left_feedforward = 0.0;

        public double right_accl = 0.0;
        public double right_demand = 0.0;
        public double right_distance = 0.0;
        public double right_feedforward = 0.0;

    }

    /**
     * internal methods beyond this point
     **/

    private static double rotationsToInches(double rotations) {
        return rotations * Math.PI * Constants.DRIVE_WHEEL_DIAMETER_INCHES;
    }

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    private static double inchesToRotations(double inches) {
        return inches / (Math.PI * Constants.DRIVE_WHEEL_DIAMETER_INCHES);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 4096.0 / 10.0;
    }

    private static double inchesPerSecondToRadiansPerSecond(double in_sec) {
        return in_sec / (Constants.DRIVE_WHEEL_DIAMETER_INCHES * Math.PI) * 2 * Math.PI;
    }

    private static double rpmToTicksPer100ms(double rpm) {
        return ((rpm * 512.0) / 75.0);
    }

    private DriveSignal arcadeDrive(double xSpeed, double zRotation) {
        double leftMotorOutput;
        double rightMotorOutput;

        double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

        if (xSpeed >= 0.0) {
            // First quadrant, else second quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            } else {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (zRotation >= 0.0) {
                leftMotorOutput = xSpeed + zRotation;
                rightMotorOutput = maxInput;
            } else {
                leftMotorOutput = maxInput;
                rightMotorOutput = xSpeed - zRotation;
            }
        }
        return new DriveSignal(rightMotorOutput, leftMotorOutput);
    }
}
