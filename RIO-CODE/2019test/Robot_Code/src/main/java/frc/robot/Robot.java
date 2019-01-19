/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.lib.loops.Looper;
import frc.lib.statemachine.StateMachine;
import frc.lib.util.VersionData;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Logger;
import frc.robot.subsystems.PoseEstimator;
import frc.robot.statemachines.*;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

    public SubsystemManager manager = new SubsystemManager(Arrays.asList(
        Drive.getInstance(),
        PoseEstimator.getInstance(),
        Logger.getInstance()
        ));
    public Looper enabled = new Looper();
    public Looper disabled = new Looper();
    public OI oi;

    @Override
    public void robotInit() {
        manager.registerEnabledLoops(enabled);
        manager.registerDisabledLoops(disabled);
        VersionData.WriteBuildInfoToDashboard();
        oi = new OI();
        Logger.getInstance().addNumberKeys(Constants.NUMBER_KEYS);
        Logger.getInstance().addStringKeys(Constants.STRING_KEYS);
        Logger.getInstance().addDSKeys(Constants.DS_KEYS);
    }

    @Override
    public void robotPeriodic() {
        manager.outputTelemetry();
    }

    @Override
    public void disabledInit() {
        StateMachine.assertStop();
        enabled.stop();
        disabled.start();
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        Drive.getInstance().reset();
        PoseEstimator.getInstance().reset();
        disabled.stop();
        enabled.start();
        //StateMachine.runMachine(new TestMach());
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        Drive.getInstance().reset();
        PoseEstimator.getInstance().reset();
        disabled.stop();
        enabled.start();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testInit() {
        disabled.stop();
        enabled.start();
    }

    @Override
    public void testPeriodic() {
    }

}
