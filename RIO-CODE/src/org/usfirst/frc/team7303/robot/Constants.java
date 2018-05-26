package org.usfirst.frc.team7303.robot;

public class Constants {
    //--------------------------\\
    //--General constants here--||
    //--------------------------//

    //logging things
    public static final String ROBOT_NAME = "roamer";
    public static String DRIVE_PATH_1 = "/media/sda"; // top usb port
    public static String DRIVE_PATH_2 = "/media/sdb"; // bottom usb port
    public static double LOGGING_UPDATE_RATE = 0.010;

    //Pure pursuit related values
    public static final boolean ENABLE_MP_TEST_MODE = false;
    public static final double DRIVETRAIN_UPDATE_RATE = 0.010;
    public static int OBSERVATION_BUFFER_SIZE = 10; //size of pose observation buffer
    public static double TRACK_WIDTH_INCHES = 23.5;
    public static double TRACK_SCRUB_FACTOR = 0.5;
    public static double WHEEL_DIAMETER = 6.0;
    public static double COUNTS_PER_REV = 4096; //encoder counts per revolution
    public static double PATH_FOLLOWING_LOOKAHEAD = 24.0; // lookahead in inches
    public static double PATH_FOLLOWING_MAX_VELOCITY = 200.0; //overall max velocity - includes turns - in inches/sec
    public static double PATH_FOLLOWING_MAX_ACCELERATION = 42.0; // overall max acceleration - includes turns - in inches/sec^2
    public static final double MP_TEST_SPEED = 152;


    //Drivetrain constants for teleop
    private static double TELEOP_DEADBAND = 0.1500; //nominal deadband 0.1500
    private static double TELEOP_Y_PERCENTAGE = 0.8500; //nominal decrease to y ouput percentage 0.7500
    private static double TELEOP_Y_CUT_PERCENTAGE = 0.5000; //nominal fine adjust y cut 0.5000
    private static double TELEOP_X_PERCENTAGE = 1.000; //nominal decrease to x output percentage 1.0000
    private static double TELEOP_X_CUT_PERCENTAGE = 1.0000; //nominal fine adjust x cut percentage
    private static double TELEOP_Z_PERCENTAGE = 0.5000; //nominal decrease to z output percentage

    //Gyrolock constants
    private static double GYROLOCK_KP = 0.0330; //nominal 0.0330
    private static double GYROLOCK_KI = 0.0000; //nominal 0.0000
    private static double GYROLOCK_KD = 0.0550; //nominal 0.0550
    private static double GYROLOCK_TOL = 1.0000; //nominal 1.0000
    private static double GYROLOCK_LIM = 0.6500; //nominal 0.6500

    public static double getTeleopDeadband() {
        return TELEOP_DEADBAND;
    }

    public static double getTeleopYPercentage() {
        return TELEOP_Y_PERCENTAGE;
    }

    public static double getTeleopYCutPercentage() {
        return TELEOP_Y_CUT_PERCENTAGE;
    }

    public static double getTeleopXPercentage() {
        return TELEOP_X_PERCENTAGE;
    }

    public static double getTeleopXCutPercentage() {
        return TELEOP_X_CUT_PERCENTAGE;
    }

    public static double getTeleopZPercentage() {
        return TELEOP_Z_PERCENTAGE;
    }

    public static double getGyrolockKp() {
        return GYROLOCK_KP;
    }

    public static double getGyrolockKi(){
        return GYROLOCK_KI;
    }

    public static double getGyrolockKd() {
        return GYROLOCK_KD;
    }

    public static double getGyrolockTol() {
        return GYROLOCK_TOL;
    }

    public static double getGyrolockLim() {
        return GYROLOCK_LIM;
    }
}
