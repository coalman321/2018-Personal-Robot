/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7303.robot;

import org.usfirst.frc.team7303.subsystems.RobotDriveV4.PoseEstimator;
import org.usfirst.frc.team7303.subsystems.RobotDriveV4.RobotDriveV4;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap 
{

    public static PoseEstimator robotPose;
    public static RobotDriveV4 robotDriveV4;


    static {
        robotPose = new PoseEstimator();
        robotDriveV4 = new RobotDriveV4();
    }

}
