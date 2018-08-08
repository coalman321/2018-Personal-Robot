package frc.robot;

public class Constants {

    /*
     * ----------------------
     * || Shared Constants ||
     * ----------------------
     */

    private static boolean isCompBot = true; //change this to use competition or non-competition constants
    public static String ROBOT_NAME = "Cube Crusher";

    //update times / rates
    public static double DRIVETRAIN_UPDATE_RATE = 0.010;
    public static double LOGGING_UPDATE_RATE = 0.010;
    public static double STATE_MACHINE_UPDATE_RATE = 0.010;

    //MP Test mode values
    public static boolean ENABLE_MP_TEST_MODE = false; //enables motion profiling test across all modes
    public static double MP_TEST_SPEED = 152;

    //Pure pursuit related values
    public static int OBSERVATION_BUFFER_SIZE = 10; //size of pose observation buffer
    public static double TRACK_WIDTH_INCHES = 23.5;
    public static double TRACK_SCRUB_FACTOR = 0.5;
    public static double WHEEL_DIAMETER = 6.0;
    public static double COUNTS_PER_REV = 4096; //encoder counts per revolution
    public static double PATH_FOLLOWING_LOOKAHEAD = 24.0; // lookahead in inches
    public static double PATH_FOLLOWING_MAX_VELOCITY = 200.0; //overall max velocity - includes turns - in inches/sec
    public static double PATH_FOLLOWING_MAX_ACCELERATION = 42.0; // overall max acceleration - includes turns - in inches/sec^2


    //logging directories
    public static String DRIVE_PATH_1 = "/media/sda"; // top usb port
    public static String DRIVE_PATH_2 = "/media/sdb"; // bottom usb port

}
