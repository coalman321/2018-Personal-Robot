package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.lib.util.HIDHelper;

public class Constants{

    /*
     * ---------------------- || Port Assignments || ----------------------
     */

    // Drive CAN ID assignments
    public static final int DRIVE_FRONT_LEFT_ID = 1;
    public static final int DRIVE_FRONT_RIGHT_ID = 2;
    public static final int DRIVE_REAR_LEFT_ID = 3;
    public static final int DRIVE_REAR_RIGHT_ID = 4;

    //Sensor assignments
    public static final int PIGEON_ID = 0; //on can bus. not via a talon

    /*
     * ----------------------
     * || Robot Parameters ||
     * ----------------------
     */

    //Robot Parameters
    public static final double LOOPER_DT = 0.010;
    public static final String ROBOT_NAME = "durr";
    public static final boolean IS_COMP_BOT = true;

    // Wheels
    public static final double DRIVE_WHEEL_TRACK_WIDTH_INCHES = 23.5; //TODO validate
    public static final double DRIVE_WHEEL_DIAMETER_INCHES = 7.5;//TODO validate. should be around 7.5
    public static final double DRIVE_WHEEL_RADIUS_INCHES = DRIVE_WHEEL_DIAMETER_INCHES / 2.0;
    public static final double TRACK_SCRUB_FACTOR = 1.0;  //TODO tune
    public static final double DRIVE_ENCODER_PPR = 4096; //encoder counts per revolution

    // Tuned dynamics
    public static final double ROBOT_LINEAR_INERTIA = 12.7;  // kg
    public static final double ROBOT_ANGULAR_INERTIA = 10.0;  // kg m^2 TODO tune
    public static final double ROBOT_ANGULAR_DRAG = 12.0;  // N*m / (rad/sec) TODO tune
    public static final double DRIVE_V_INTERCEPT = 1.055;  // V
    public static final double DRIVE_KV = 0.135;  // V per rad/s
    public static final double DRIVE_KA = 0.012;  // V per rad/s^2

    //
    public static final double PATH_KX = 4.0;  // units/s per unit of error
    public static final double PATH_LOOKAHEAD_TIME = 0.4;  // seconds to look ahead along the path for steering
    public static final double PATH_MIN_LOOKAHEAD_DISTANCE = 24.0;  // inches

    //Right drive PID parameters
    public static final int DRIVE_RIGHT_PID_IDX = 0; //pick one and stick to it
    public static final double DRIVE_RIGHT_KF = 0.0; //TODO tune Me!
    public static final double DRIVE_RIGHT_KP = 0.0; //TODO tune Me!
    public static final double DRIVE_RIGHT_KI = 0.0; //can be scary. generally unneeded
    public static final double DRIVE_RIGHT_KD = 0.0; //TODO tune Me!

    //Left Drive PID parameters
    public static final int DRIVE_LEFT_PID_IDX = 0; //pick one and stick to it
    public static final double DRIVE_LEFT_KF = 0.0; //TODO tune Me!
    public static final double DRIVE_LEFT_KP = 0.0; //TODO tune Me!
    public static final double DRIVE_LEFT_KI = 0.0; //can be scary. generally unneeded
    public static final double DRIVE_LEFT_KD = 0.0; //TODO tune Me!


    /*
     * ------------------------
     * || Logging Parameters ||
     * ------------------------
     */

    //Logging Parameters
    public static final String DATA_SEPERATOR = ","; //logging data file delimiter
    public static final String[] NUMBER_KEYS = {};
    public static final String[] STRING_KEYS = {}; //no string keys currently
    public static final String[] DS_KEYS = {"Robot_Pose/X", "Robot_Pose/Y", "Robot_Pose/Theta", "Arm", "Arm", "Arm", "Arm", "Arm" };

    /*
     * ------------------------
     * || Control Parameters ||
     * ------------------------
     */

    //Calibration parameters
    public static final boolean ENABLE_MP_TEST_MODE = false;
    public static final int MP_TEST_SPEED = 5; //inches per second

    //Joystick controls
    public static final Joystick MASTER = new Joystick(0);
    public static final HIDHelper.HIDConstants MASTER_STICK = new HIDHelper.HIDConstants(MASTER, 0.15, 1.0, 1.0, 0.5, 2);

}