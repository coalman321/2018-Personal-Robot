package frc.robot.planners;

import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Rotation2d;
import frc.lib.geometry.Translation2d;
import frc.lib.geometry.Twist2d;
import frc.lib.trajectory.TimedView;
import frc.lib.trajectory.TrajectoryIterator;
import frc.lib.trajectory.timing.CentripetalAccelerationConstraint;
import frc.robot.Constants;
import frc.robot.Kinematics;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class DriveMotionPlannerTest {

    //TODO re-enable these tests

    @Test
    public void testForwardSwerveRight() {
        System.out.println("testing Pure pursuit forward swerve right");
        DriveMotionPlanner motion_planner = new DriveMotionPlanner();
        motion_planner.setFollowerType(DriveMotionPlanner.FollowerType.PURE_PURSUIT);
        motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(motion_planner.generateTrajectory
                (false, Arrays.asList(new Pose2d(new Translation2d(0.0, 0.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(120.0, -36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(240.0, -36.0), Rotation2d.identity())),
                        Arrays.asList(new CentripetalAccelerationConstraint(120.0)),
                        120.0, 120.0, 10.0))));

        double t = 0.0;
        Pose2d pose = motion_planner.setpoint().state().getPose();
        while (!motion_planner.isDone()) {
            motion_planner.update(t, pose);
            pose = motion_planner.mSetpoint.state().getPose();//.transformBy(new Pose2d(new Translation2d(0.0, 1.0),
            // Rotation2d.fromDegrees(2.0)));

            System.out.println(t + "," + motion_planner.toCSV());
            t += 0.01;// + (2.0 * Math.random() - 1.0) * 0.002;
        }
    }

    @Test
    public void testForwardSwerveLeft() {
        System.out.println("testing PID forward swerve left");
        System.out.println("time, velocity, accel, ");
        DriveMotionPlanner motion_planner = new DriveMotionPlanner();
        motion_planner.setFollowerType(DriveMotionPlanner.FollowerType.PID);
        motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(motion_planner.generateTrajectory
                (false, Arrays.asList(new Pose2d(new Translation2d(0.0, 0.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(120.0, 36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(240.0, 0.0), Rotation2d.identity())),
                        null,
                        120.0, 120.0, 10.0))));
        double t = 0.0;
        Pose2d pose = motion_planner.setpoint().state().getPose();
        while (!motion_planner.isDone()) {
            motion_planner.update(t, pose);
            pose = motion_planner.mSetpoint.state().getPose();//.transformBy(new Pose2d(new Translation2d(0.0, -1.0),
            // Rotation2d.fromDegrees(-2.0)));
            System.out.println(t + "," + motion_planner.toCSV());
            t += 0.01;
        }
    }

    @Test
    public void testReverseSwerveLeft() {
        System.out.println("testing nonlinear reverse swerve left");
        DriveMotionPlanner motion_planner = new DriveMotionPlanner();
        motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(motion_planner.generateTrajectory
                (true, Arrays.asList(new Pose2d(new Translation2d(240.0, 0.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(120.0, 36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(0.0, 0.0), Rotation2d.identity())),
                        null,
                        120.0, 120.0, 10.0))));
        double t = 0.0;
        Pose2d pose = motion_planner.setpoint().state().getPose();
        while (!motion_planner.isDone()) {
            motion_planner.update(t, pose);
            pose = motion_planner.mSetpoint.state().getPose();
            System.out.println(t + "," + motion_planner.toCSV());
            t += 0.01;
        }
    }

    @Test
    public void testForwardReverseSame() {
        System.out.println("testing pure pursuit forward reverse same");
        DriveMotionPlanner fwd_motion_planner = new DriveMotionPlanner();
        fwd_motion_planner.setFollowerType(DriveMotionPlanner.FollowerType.PURE_PURSUIT);
        fwd_motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(fwd_motion_planner.generateTrajectory
                (false, Arrays.asList(new Pose2d(new Translation2d(10.0, 10.0), Rotation2d.fromDegrees(5.0)),
                        new Pose2d(new Translation2d(120.0, 36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(230.0, 10.0), Rotation2d.fromDegrees(-5.0))),
                        null,
                        120.0, 120.0, 5.0))));
        DriveMotionPlanner rev_motion_planner = new DriveMotionPlanner();
        rev_motion_planner.setFollowerType(DriveMotionPlanner.FollowerType.PURE_PURSUIT);
        rev_motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(rev_motion_planner.generateTrajectory
                (true, Arrays.asList(new Pose2d(new Translation2d(230.0, 10.0), Rotation2d.fromDegrees(-5.0)),
                        new Pose2d(new Translation2d(120.0, 36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(10.0, 10.0), Rotation2d.fromDegrees(5.0))),
                        null,
                        120.0, 120.0, 5.0))));

        final double dt = 0.01;
        double t = 0.0;
        Pose2d start_error_fwd = new Pose2d(2.0, 3.0, Rotation2d.fromDegrees(2.0));
        Pose2d start_error_rev = new Pose2d(-2.0, 3.0, Rotation2d.fromDegrees(-2.0));
        Pose2d fwd_pose = fwd_motion_planner.setpoint().state().getPose().transformBy(start_error_fwd);
        Pose2d rev_pose = rev_motion_planner.setpoint().state().getPose().transformBy(start_error_rev);
        while (!fwd_motion_planner.isDone() || !rev_motion_planner.isDone()) {
            DriveMotionPlanner.Output fwd_output = fwd_motion_planner.update(t, fwd_pose);
            Twist2d fwd_delta = Kinematics.forwardKinematics2(fwd_output.linear_velocity , fwd_output.angular_velocity);
            fwd_pose = fwd_pose.transformBy(Pose2d.exp(fwd_delta));
            //System.out.println("FWD Delta: " + fwd_delta + ", Pose: " + fwd_pose);
            DriveMotionPlanner.Output rev_output = rev_motion_planner.update(t, rev_pose);
            Twist2d rev_delta = Kinematics.forwardKinematics2(rev_output.linear_velocity, rev_output.angular_velocity);
            rev_pose = rev_pose.transformBy(Pose2d.exp(rev_delta));
            //System.out.println("REV Delta: " + rev_delta + ", Pose: " + rev_pose);
            System.out.println(fwd_motion_planner.toCSV() + "," + rev_motion_planner.toCSV());
            t += dt;
        }
    }

    @Test
    public void testFollowerReachesGoal() {
        System.out.println("testing nonlinear reaches goal");
        final DriveMotionPlanner motion_planner = new DriveMotionPlanner();
        motion_planner.setFollowerType(DriveMotionPlanner.FollowerType.NONLINEAR_FEEDBACK);
        motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(motion_planner.generateTrajectory
                (false, Arrays.asList(new Pose2d(new Translation2d(0.0, 0.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(120.0, -36.0), Rotation2d.identity()),
                        new Pose2d(new Translation2d(240.0, -36.0), Rotation2d.identity())),
                        null,
                        120.0, 120.0, 10.0))));
        final double dt = 0.01;
        double t = 0.0;
        Pose2d initial_error = new Pose2d(2.0, 3.0, Rotation2d.fromDegrees(3.5));
        Pose2d pose = motion_planner.setpoint().state().getPose().transformBy(initial_error);
        while (!motion_planner.isDone()) {
            DriveMotionPlanner.Output output = motion_planner.update(t, pose);
            Twist2d delta = Kinematics.forwardKinematics2(output.linear_velocity, output.angular_velocity);
            // Add some systemic error.
            delta = new Twist2d(delta.dx * 1.0, delta.dy * 1.0, delta.dtheta * 1.05);
            pose = pose.transformBy(Pose2d.exp(delta));
            t += dt;
            System.out.println(motion_planner.setpoint().toCSV() + "," + pose.toCSV());
        }
        System.out.println(pose);
    }

    //@Test
    public void testVoltages() {
        System.out.println("testing voltages");
        final DriveMotionPlanner motion_planner = new DriveMotionPlanner();
        motion_planner.setTrajectory(new TrajectoryIterator<>(new TimedView<>(motion_planner.generateTrajectory
                (false, Arrays.asList(Pose2d.identity(), Pose2d.fromTranslation(new Translation2d(48.0, 0.0)),
                        new Pose2d(new Translation2d(96.0, 48.0), Rotation2d.fromDegrees(90.0)),
                        new Pose2d(new Translation2d(96.0, 96.0), Rotation2d.fromDegrees(90.0))), null,
                        48.0, 48.0, 10.0))));
        double t = 0.0;
        Pose2d pose = motion_planner.setpoint().state().getPose().transformBy(new Pose2d(Translation2d.identity(),
                Rotation2d.fromDegrees(180.0)));
        while (!motion_planner.isDone()) {
            motion_planner.update(t, pose);
            pose = motion_planner.mSetpoint.state().getPose();
            System.out.println(t + "," + motion_planner.toCSV());
            t += 0.01;
        }
    }
}
