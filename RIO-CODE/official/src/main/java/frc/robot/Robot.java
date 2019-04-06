package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib.util.SocketTables;

public class Robot extends TimedRobot {

    @Override
    public void robotInit() {
        System.out.println("Q1:" + SocketTables.getInstance().queryValue("{\"request\": \"UPDATE\", \"key\": \"test1\", \"value\": 4}"));
        System.out.println("Q2:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q3:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q4:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q5:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q6:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q7:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q8:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q9:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q10:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
        System.out.println("Q11:" + SocketTables.getInstance().queryValue("{\"request\": \"GET\", \"key\": \"test1\"}"));
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
