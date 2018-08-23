package frc.robot;

import frc.lib.loops.Looper;
import frc.robot.subsystems.RobotStateEstimator;
import org.junit.jupiter.api.Test;

public class DriveSystemTest {

    //TODO enable these tests
    //they may not work due to wpilibj calls

    //@Test
    public void testRobotPose(){
        RobotStateEstimator inst = RobotStateEstimator.getInstance();
        Looper enabledLoop = new Looper();
        inst.registerEnabledLoops(enabledLoop);
        enabledLoop.start();

    }

    public void testRobotDrive(){

    }

    public void testDriveSystem(){

    }


}
