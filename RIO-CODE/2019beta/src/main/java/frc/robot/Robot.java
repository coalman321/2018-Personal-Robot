/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.loops.Looper;
import frc.robot.paths.TrajectoryGenerator;
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
            Drive.getInstance()
    ));
    private Drive mDrive = Drive.getInstance();
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();

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
        mSubsystemManager.outputToSmartDashboard();
        mSubsystemManager.writeToLog();
    }

    @Override
    public void disabledInit() {
        mEnabledLooper.stop();
        mDisabledLooper.start();
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        mDisabledLooper.stop();
        mEnabledLooper.start();

    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        mDisabledLooper.stop();
        mEnabledLooper.start();
    }


    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    public void testInit(){
        mDisabledLooper.stop();
        mEnabledLooper.start();
    }

    @Override
    public void testPeriodic() {
    }
}

