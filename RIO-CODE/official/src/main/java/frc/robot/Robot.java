package frc.robot;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.util.SocketTables;

public class Robot extends TimedRobot {

    @Override
    public void robotInit() {
        double start = Timer.getFPGATimestamp();
        SocketTables.getInstance().putData("test1", "8");
        System.out.println(Timer.getFPGATimestamp() - start);

        start = Timer.getFPGATimestamp();
        System.out.println("Q2:" + SocketTables.getInstance().getData("test1"));
        System.out.println(Timer.getFPGATimestamp() - start);

        start = Timer.getFPGATimestamp();
        System.out.println("Q3:" + SocketTables.getInstance().getData("test2"));
        System.out.println(Timer.getFPGATimestamp() - start);

        start = Timer.getFPGATimestamp();
        System.out.println("Q4:" + SocketTables.getInstance().getData("test3"));
        System.out.println(Timer.getFPGATimestamp() - start);
    }

    @Override
    public void robotPeriodic() {

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
