package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.util.SocketTables;
import frc.lib.util.Util;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Subsystem;
import frc.robot.subsystems.TestSystem1;
import frc.robot.subsystems.TestSystem2;

import java.util.Arrays;

public class Robot extends TimedRobot {

    SubsystemManager manager;
    double start;

    @Override
    public void robotInit() {
        manager = new SubsystemManager(Arrays.asList(
            TestSystem1.getInstance(),
            TestSystem2.getInstance(),
            Drive.getInstance()
        ));
    }

    @Override
    public void robotPeriodic() {
        TestSystem1.getInstance().incrementVar1();
        TestSystem2.getInstance().toggleVar2();
        start = Timer.getFPGATimestamp();
        manager.logTelemetry();
        System.out.println("Operation Time: " + (Timer.getFPGATimestamp() - start));
        SocketTables.getInstance().putData("hi", "hello");
        System.out.println(SocketTables.getInstance().getData("hi"));
    }

    @Override
    public void disabledInit() {

    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void autonomousInit() {

    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {

    }

    @Override
    public void teleopPeriodic() {

    }

    @Override
    public void testInit() {

    }

    @Override
    public void testPeriodic() {

    }

}
