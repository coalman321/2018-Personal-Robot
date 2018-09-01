/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Pose2dWithCurvature;
import frc.lib.geometry.State;
import frc.lib.loops.Looper;
import frc.lib.trajectory.TimedView;
import frc.lib.trajectory.TrajectoryIterator;
import frc.lib.trajectory.timing.TimedState;
import frc.robot.subsystems.Communication;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.RobotStateEstimator;

import java.util.Arrays;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    //private TrajectoryGenerator mTrajectoryGenerator = TrajectoryGenerator.getInstance();
    private final SubsystemManager mSubsystemManager = new SubsystemManager(Arrays.asList(
            RobotStateEstimator.getInstance(),
            Drive.getInstance(),
            Communication.getInstance()
    ));
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();
    private TrajectoryIterator mWantedTrajectory;

    public static OI mOI = OI.getInstance();

    @Override
    public void robotInit() {
        mSubsystemManager.registerEnabledLoops(mEnabledLooper);
        mSubsystemManager.registerDisabledLoops(mDisabledLooper);
        mDisabledLooper.start();
        NetworkTableInstance.getDefault().setUpdateRate(0.01);

    }

    /**
    * This function is called every robot packet, no matter the mode. Use
    * this for items like diagnostics that you want ran during disabled,
    * autonomous, teleoperated and test.
    *
    * <p>This runs after the mode specific periodic functions, but before
    * LiveWindow and SmartDashboard integrated updating.
    */
    @Override
    public void robotPeriodic() {

    }

    @Override
    public void disabledInit() {
        mEnabledLooper.stop();
        mDisabledLooper.start();
    }

    @Override
    public void disabledPeriodic() {
        runCommunicationUpdates();
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        mDisabledLooper.stop();
        mEnabledLooper.start();

    }

    @Override
    public void autonomousPeriodic() {
        runCommunicationUpdates();
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        mDisabledLooper.stop();
        mEnabledLooper.start();
    }


    @Override
    public void teleopPeriodic() {
        runCommunicationUpdates();
        Scheduler.getInstance().run();
    }

    public void testInit(){
        runCommunicationUpdates();
    }

    @Override
    public void testPeriodic() {

    }

    public void runCommunicationUpdates(){
        if(Communication.getInstance().getPeriodicIO().wantPoseReset){
            RobotState.getInstance().reset(Timer.getFPGATimestamp(), Communication.getInstance().getPeriodicIO().wantedResetPose);
        }

        //if want to receive trajectory
        if(Communication.getInstance().getPeriodicIO().wantToRecieveTrajectory){
            mWantedTrajectory = new TrajectoryIterator<>(new TimedView<>(Drive.getInstance().generateTrajectory(
                    false,
                    Communication.getInstance().getPeriodicIO().wantedTrajectory,
                    null,
                    Constants.kRobotMaxVelocity,
                    Constants.kRobotMaxAccel,
                    Constants.kRobotMaxVoltage))); // generate trajectory from data
        }

        //if want to run trajectory and not moving and trajectory is not null
        if(Communication.getInstance().getPeriodicIO().wantToRunTrajectory &&
                Math.abs(Drive.getInstance().getLinearVelocity()) < 1.00 &&
                mWantedTrajectory != null){
            Drive.getInstance().setTrajectory(mWantedTrajectory); //run new trajectory
        }

        mSubsystemManager.outputTelemetry();
        mSubsystemManager.writeToLog();
    }
}

