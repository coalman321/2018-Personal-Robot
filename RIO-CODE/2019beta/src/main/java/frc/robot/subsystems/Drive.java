package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Pose2dWithCurvature;
import frc.lib.geometry.Rotation2d;
import frc.lib.loops.Loop;
import frc.lib.trajectory.PurePursuitController;
import frc.lib.trajectory.TrajectoryIterator;
import frc.lib.trajectory.timing.TimedState;
import frc.lib.util.DriveSignal;
import frc.lib.util.HIDHelper;
import frc.lib.util.ReflectingCSVWriter;
import frc.robot.Constants;
import frc.robot.RobotState;
import frc.robot.planners.DriveMotionPlanner;

public class Drive extends Subsystem {

    //used internally for data

    //construct one and only 1 instance of this class
    private static Drive m_DriveInstance = new Drive();
    private DriveControlState mDriveControlState = DriveControlState.OPEN_LOOP;
    private DriveMotionPlanner mMotionPlanner;
    private PurePursuitController pathFollowingController;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter;
    private PeriodicIO periodicIO;
    private boolean mOverrideTrajectory = false;
    private double[] operatorInput = {0, 0, 0}; //last input set from joystick update
    private final Loop mLoop = new Loop() {

        @Override
        public void onStart(double timestamp) {
            synchronized (Drive.this) {
                startLogging();
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                if (Constants.ENABLE_MP_TEST_MODE) mDriveControlState = DriveControlState.PROFILING_TEST;
                switch (mDriveControlState) {
                    case PATH_FOLLOWING:
                        updatePathFollower();
                        break;

                    case PROFILING_TEST:
                        if (DriverStation.getInstance().isAutonomous()) {
                            //driveTank(Constants.MP_TEST_SPEED, Constants.MP_TEST_SPEED);
                        }
                        break;

                    case OPEN_LOOP:
                        if (DriverStation.getInstance().isOperatorControl())
                            operatorInput = HIDHelper.getAdjStick(Constants.MASTER_STICK);
                        else operatorInput = new double[]{0, 0, 0};
                        break;

                    default:
                        System.out.println("unexpected control state ");;
                }


            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    };
    private Rotation2d mGyroOffset;

    private Drive() {
        periodicIO = new PeriodicIO();
        mCSVWriter = new ReflectingCSVWriter<PeriodicIO>("", PeriodicIO.class);
        reset();
        mMotionPlanner = new DriveMotionPlanner();
    }

    public static Drive getInstance() {
        return m_DriveInstance;
    }

    private static double rotationsToInches(double rotations) {
        return rotations * (Constants.kDriveWheelDiameterInches * Math.PI);
    }

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    private static double inchesToRotations(double inches) {
        return inches / (Constants.kDriveWheelDiameterInches * Math.PI);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 4096.0 / 10.0;
    }

    public synchronized Rotation2d getHeading() {
        return periodicIO.gyro_heading;
    }

    public synchronized void setHeading(Rotation2d heading) {
        System.out.println("SET HEADING: " + heading.getDegrees());

        mGyroOffset = heading.rotateBy(Rotation2d.fromDegrees(0).inverse()); //TODO replace zero with gyro source
        System.out.println("Gyro offset: " + mGyroOffset.getDegrees());

        periodicIO.gyro_heading = heading;
    }

    public double getLeftEncoder() {
        //TODO implement value retrieval
        return 0;
    }

    public double getLeftVelocity() {
        //TODO implement value retrieval
        return 0;
    }

    public double getRightEncoder() {
        //TODO implement value retrieval
        return 0;
    }

    public double getRightVelocity() {
        //TODO implement value retrieval
        return 0;
    }

    public void setOperatorInput(double[] input) {
        operatorInput = input;
    }

    public void reset() {
        //TODO add reset with sensor impl

    }

    public double getLeftEncoderRotations() {
        return periodicIO.left_position_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public double getRightEncoderRotations() {
        return periodicIO.right_position_ticks / Constants.DRIVE_ENCODER_PPR;
    }

    public double getLeftEncoderDistance() {
        return rotationsToInches(getLeftEncoderRotations());
    }

    public double getRightEncoderDistance() {
        return rotationsToInches(getRightEncoderRotations());
    }

    public double getRightVelocityNativeUnits() {
        return periodicIO.right_velocity_ticks_per_100ms;
    }

    public double getRightLinearVelocity() {
        return rotationsToInches(getRightVelocityNativeUnits() * 10.0 / Constants.DRIVE_ENCODER_PPR);
    }

    public double getLeftVelocityNativeUnits() {
        return periodicIO.left_velocity_ticks_per_100ms;
    }

    public double getLeftLinearVelocity() {
        return rotationsToInches(getLeftVelocityNativeUnits() * 10.0 / Constants.DRIVE_ENCODER_PPR);
    }

    public double getLinearVelocity() {
        return (getLeftLinearVelocity() + getRightLinearVelocity()) / 2.0;
    }

    public double getAngularVelocity() {
        return (getRightLinearVelocity() - getLeftLinearVelocity()) / Constants.kDriveWheelTrackWidthInches;
    }

    public void overrideTrajectory(boolean value) {
        mOverrideTrajectory = value;
    }

    private void updatePathFollower() {
        if (mDriveControlState == DriveControlState.PATH_FOLLOWING) {
            final double now = Timer.getFPGATimestamp();

            DriveMotionPlanner.Output output = mMotionPlanner.update(now, RobotState.getInstance().getFieldToVehicle(now));

            // DriveSignal signal = new DriveSignal(demand.left_feedforward_voltage / 12.0, demand.right_feedforward_voltage / 12.0);

            periodicIO.error = mMotionPlanner.error();
            periodicIO.path_setpoint = mMotionPlanner.setpoint();

            if (!mOverrideTrajectory) {
                setVelocity(new DriveSignal(radiansPerSecondToTicksPer100ms(output.left_velocity), radiansPerSecondToTicksPer100ms(output.right_velocity)),
                        new DriveSignal(output.left_feedforward_voltage / 12.0, output.right_feedforward_voltage / 12.0));

                periodicIO.left_accel = radiansPerSecondToTicksPer100ms(output.left_accel) / 1000.0;
                periodicIO.right_accel = radiansPerSecondToTicksPer100ms(output.right_accel) / 1000.0;
            } else {
                setVelocity(DriveSignal.BRAKE, DriveSignal.BRAKE);
                periodicIO.left_accel = periodicIO.right_accel = 0.0;
            }
        } else {
            DriverStation.reportError("Drive is not in path following state", false);
        }
    }

    /**
     * Configure talons for open loop control
     */
    public synchronized void setOpenLoop(DriveSignal signal) {
        if (mDriveControlState != DriveControlState.OPEN_LOOP) {
            System.out.println("Switching to open loop");
            /*System.out.println(signal);*/
            mDriveControlState = DriveControlState.OPEN_LOOP;
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
        periodicIO.left_feedforward = 0.0;
        periodicIO.right_feedforward = 0.0;
    }

    /**
     * Configures talons for velocity control
     */
    public synchronized void setVelocity(DriveSignal signal, DriveSignal feedforward) {
        if (mDriveControlState != DriveControlState.PATH_FOLLOWING) {
            // We entered a velocity control state.
            //TODO configure motor control for velocity

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
            return false;
        }
        return mMotionPlanner.isDone() || mOverrideTrajectory;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double prevLeftTicks = periodicIO.left_position_ticks;
        double prevRightTicks = periodicIO.right_position_ticks;
        periodicIO.left_position_ticks = 0; //TODO Add data source replacing zero
        periodicIO.right_position_ticks = 0; //TODO Add data source replacing zero
        periodicIO.left_velocity_ticks_per_100ms = 0; //TODO Add data source replacing zero
        periodicIO.right_velocity_ticks_per_100ms = 0; //TODO Add data source replacing zero
        periodicIO.gyro_heading = Rotation2d.fromDegrees(0).rotateBy(mGyroOffset); //TODO Add data source replacing zero

        double deltaLeftTicks = ((periodicIO.left_position_ticks - prevLeftTicks) / 4096.0) * Math.PI;
        if (deltaLeftTicks > 0.0) {
            periodicIO.left_distance += deltaLeftTicks * Constants.kDriveWheelDiameterInches;
        } else {
            periodicIO.left_distance += deltaLeftTicks * Constants.kDriveWheelDiameterInches;
        }

        double deltaRightTicks = ((periodicIO.right_position_ticks - prevRightTicks) / 4096.0) * Math.PI;
        if (deltaRightTicks > 0.0) {
            periodicIO.right_distance += deltaRightTicks * Constants.kDriveWheelDiameterInches;
        } else {
            periodicIO.right_distance += deltaRightTicks * Constants.kDriveWheelDiameterInches;
        }

        if (mCSVWriter != null) {
            mCSVWriter.add(periodicIO);
        }

        // System.out.println("control state: " + mDriveControlState + ", left: " + periodicIO.left_demand + ", right: " + periodicIO.right_demand);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if (mDriveControlState == DriveControlState.OPEN_LOOP) {
            //TODO write open loop outputs
            //mLeftMaster.set(ControlMode.PercentOutput, periodicIO.left_demand, DemandType.ArbitraryFeedForward, 0.0);
            //mRightMaster.set(ControlMode.PercentOutput, periodicIO.right_demand, DemandType.ArbitraryFeedForward, 0.0);
        } else {
            //TODO write velocity control mode outputs
            //mLeftMaster.set(ControlMode.Velocity, periodicIO.left_demand, DemandType.ArbitraryFeedForward,
            //       periodicIO.left_feedforward + Constants.kDriveLowGearVelocityKd * periodicIO.left_accel / 1023.0);
            //mRightMaster.set(ControlMode.Velocity, periodicIO.right_demand, DemandType.ArbitraryFeedForward,
            //       periodicIO.right_feedforward + Constants.kDriveLowGearVelocityKd * periodicIO.right_accel / 1023.0);
        }
    }


    public void outputTelemetry() {

        if (mCSVWriter != null) {
            mCSVWriter.add(periodicIO);
            mCSVWriter.write();
        }
    }

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
            mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/DRIVE-LOGS.csv", PeriodicIO.class);
        }
    }

    @Override
    public void stop() {

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
        public double left_distance;
        public double right_distance;
        public int left_velocity_ticks_per_100ms;
        public int right_velocity_ticks_per_100ms;
        public Rotation2d gyro_heading = Rotation2d.identity();
        public Pose2d error = Pose2d.identity();

        // OUTPUTS
        public double left_demand;
        public double right_demand;
        public double left_accel;
        public double right_accel;
        public double left_feedforward;
        public double right_feedforward;
        public TimedState<Pose2dWithCurvature> path_setpoint = new TimedState<Pose2dWithCurvature>(Pose2dWithCurvature.identity());
    }

}
