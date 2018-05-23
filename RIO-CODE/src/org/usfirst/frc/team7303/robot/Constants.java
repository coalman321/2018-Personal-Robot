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
    public static int OBSERVATION_BUFFER_SIZE = 10; //size of pose observation buffer
    public static double TRACK_WIDTH_INCHES = 23.5;
    public static double TRACK_SCRUB_FACTOR = 0.5;
    public static double WHEEL_DIAMETER = 6.0;
    public static double COUNTS_PER_REV = 4096; //encoder counts per revolution
    public static double PATH_FOLLOWING_LOOKAHEAD = 24.0; // lookahead in inches
    public static double PATH_FOLLOWING_MAX_VELOCITY = 200.0; //overall max velocity - includes turns - in inches/sec
    public static double PATH_FOLLOWING_MAX_ACCELERATION = 42.0; // overall max acceleration - includes turns - in inches/sec^2


}
