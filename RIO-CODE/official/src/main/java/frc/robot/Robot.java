package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib.geometry.Pose2d;
import frc.lib.geometry.Rotation2d;
import frc.lib.geometry.Translation2d;
import frc.lib.util.ReflectingLogger;

public class Robot extends TimedRobot {

    PeriodicIO periodicIO = new PeriodicIO();
    ReflectingLogger<PeriodicIO> logger;

    @Override
    public void robotInit() {
        logger = new ReflectingLogger<>("", PeriodicIO.class);

    }

   @Override
    public void robotPeriodic() {
        periodicIO.b += 1;
        periodicIO.a -= 1;
        periodicIO.pose = periodicIO.pose.transformBy(new Pose2d(new Translation2d(1, -1), Rotation2d.fromDegrees(10)));
        logger.update(periodicIO);
        logger.write();

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

    public class PeriodicIO{
        public int a = 0;
        public double b = 10.1;
        public Pose2d pose =  new Pose2d(10, 10, Rotation2d.fromDegrees(10));
    }

}
