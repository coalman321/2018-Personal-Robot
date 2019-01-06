/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib.loops.Looper;
import frc.lib.util.VersionData;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Logger;

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
        Logger.getInstance()
        ));
    public Looper enabled = new Looper();
    public Looper disabled = new Looper();

    @Override
    public void robotInit() {
        manager.registerEnabledLoops(enabled);
        manager.registerDisabledLoops(disabled);
        VersionData.WriteBuildInfoToDashboard();
    }

    @Override
    public void robotPeriodic() {
        manager.outputTelemetry();
    }

    @Override
    public void disabledInit() {
        enabled.stop();
        disabled.start();
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        disabled.stop();
        enabled.start();
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
        disabled.stop();
        enabled.start();
    }

    @Override
    public void teleopPeriodic() {
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
