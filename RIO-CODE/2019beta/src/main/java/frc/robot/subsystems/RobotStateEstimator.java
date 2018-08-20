package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Rotation2d;
import frc.lib.geometry.Twist2d;
import frc.lib.loops.ILooper;
import frc.lib.loops.Loop;
import frc.robot.Kinematics;
import frc.robot.RobotState;


public class RobotStateEstimator extends Subsystem {

    static RobotStateEstimator instance_ = new RobotStateEstimator();
    private RobotState robot_state_ = RobotState.getInstance();
    private Drive drive_ = Drive.getInstance();
    private double left_encoder_prev_distance_ = 0.0;
    private double right_encoder_prev_distance_ = 0.0;
    private double back_encoder_prev_distance_ = 0.0;

    RobotStateEstimator() {
    }

    public static RobotStateEstimator getInstance() {
        return instance_;
    }

    /**
     * Unused. Call RobotState.outputTelemetry()
     */
    @Override
    public void outputTelemetry() {

    }

    /**
     * Unused
     */
    @Override
    public void stop() {

    }

    /**
     * Unused. Call RobotState.reset()
     */
    @Override
    public void reset() {

    }

    @Override
    public void registerEnabledLoops(ILooper looper) {
        looper.register(new EnabledLoop());
    }

    private class EnabledLoop implements Loop {
        @Override
        public synchronized void onStart(double timestamp) {
            left_encoder_prev_distance_ = drive_.getLeftEncoderDistance();
            right_encoder_prev_distance_ = drive_.getRightEncoderDistance();

        }

        @Override
        public synchronized void onLoop(double timestamp) {
            final double left_distance = drive_.getLeftEncoderDistance(); //TODO convert to new drive system
            final double right_distance = drive_.getRightEncoderDistance();
            final double delta_left = left_distance - left_encoder_prev_distance_;
            final double delta_right = right_distance - right_encoder_prev_distance_;
            final Rotation2d gyro_angle = drive_.getHeading();
            final Twist2d odometry_velocity = robot_state_.generateOdometryFromSensors(delta_left, delta_right, gyro_angle); //TODO convert to new drive system
            final Twist2d predicted_velocity = Kinematics.forwardKinematics(drive_.getLeftLinearVelocity(), drive_.getRightLinearVelocity()); //TODO convert to new drive system
            robot_state_.addObservations(timestamp, odometry_velocity, predicted_velocity);
            left_encoder_prev_distance_ = left_distance;
            right_encoder_prev_distance_ = right_distance;
        }

        @Override
        public void onStop(double timestamp) {
            // no-op
        }
    }
}