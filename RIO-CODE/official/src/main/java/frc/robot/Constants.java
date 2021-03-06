package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.lib.util.HIDHelper;

public class Constants {

    /**
     * device ID declarations ---------------------------------
     */

    //Talon IDs
    public static final int DRIVE_FRONT_LEFT_ID = 1;
    public static final int DRIVE_BACK_LEFT_ID = 2;
    public static final int DRIVE_FRONT_RIGHT_ID = 3;
    public static final int DRIVE_BACK_RIGHT_ID = 4;

    //Pigeon ID
    public static final int PIGEON_IMU_ID = 6;

    /**
     * Drivetrain tuned values --------------------------------
     */

    //Physical Constants
    public static final double DRIVE_WHEEL_TRACK_WIDTH_INCHES = 23;
    public static final double DRIVE_WHEEL_DIAMETER_INCHES = 6.225; // 6
    public static final double DRIVE_WHEEL_RADIUS_INCHES = DRIVE_WHEEL_DIAMETER_INCHES / 2.0;
    public static final double TRACK_SCRUB_FACTOR = 1.0;  // TODO tune
    public static final double ROBOT_LINEAR_INERTIA = 75;  // kg TODO tune
    public static final double ROBOT_ANGULAR_INERTIA = 10.0;  // kg m^2 TODO tune
    public static final double ROBOT_ANGULAR_DRAG = 12.0;  // N*m / (rad/sec) TODO tune

    //Path following Constants
    public static final double ROBOT_MAX_VELOCITY = 120.0; // in/s
    public static final double ROBOT_MAX_ACCEL = 120.0; // in/s^2
    public static final double ROBOT_MAX_VOLTAGE = 10.0; // V
    public static final double Path_Kx = 4.0;  //
    public static final double PATH_LOOK_AHEAD_TIME = 0.4;  // seconds to look ahead along the path for steering
    public static final double PATH_MIN_LOOK_AHEAD_DISTANCE = 24.0;  // inches

    //Electrical Constants
    public static final double DRIVE_V_INTERCEPT = 1.2;  // V //1.6 for practice......................
    public static final double DRIVE_Kv = 0.316426;  // V per rad/s -.335
    public static final double DRIVE_Ka = 0.0801;  // V per rad/s^2    0.0801
    public static final double DRIVE_VCOMP = 10.0; //V
    public static final double DRIVE_ENCODER_PPR = 4096.0; //encoder counts per revolution

    //PID Constants
    public static final double ANGLE_KP = 0.04; // 0.065;
    public static final double ANGLE_KI = 0; // 0.00125;
    public static final double ANGLE_KD = 0; // 0.1

    public static final double DRIVE_RIGHT_KP = 1.2;
    public static final double DRIVE_RIGHT_KI = 0.0;
    public static final double DRIVE_RIGHT_KD = 25; // 20 for practice bot
    public static final double DRIVE_RIGHT_KF = 0.53; //.485

    public static final double DRIVE_LEFT_KP = 1.1; // .0885
    public static final double DRIVE_LEFT_KI = 0.0; //NO INTEGRAL it masks deeper problems
    public static final double DRIVE_LEFT_KD = 25; //20 for practice
    public static final double DRIVE_LEFT_KF = 0.53;



    /**
     * Arm tuned values -----------------------------------------
     */

    // Arm Absolute Zeros
            //TODO CHECK TWICE BONEHEAD!!!!!!!
    public static double DIST_ABSOLUTE_ZERO = 1420;
    public static double PROX_ABSOLUTE_ZERO = 3146;

    //Arm Physical Constants
    public static final double PROX_LENGTH = 0.6096; // m
    public static final double DIST_LENGTH = 0.4572; // m
    public static final double PROX_MOI = 0.1;
    public static final double DIST_MOI = 0.01;

    //Electrical Constants
    public static final double PROX_Kv = 0.1; // V per rad/s
    public static final double PROX_Kt = 0.5; // V per rad/s^2 //not currently correct usage
    public static final double PROX_V_INTERCEPT = 0.1; // V

    public static final double DIST_Kv = 0.1; // V per rad/s
    public static final double DIST_Kt = 0.5; // V per rad/s^2 //not currently correct usage
    public static final double DIST_V_INTERCEPT = 0.1; // V

    //PID Constants
    public static final double ARM_PROX_KP = 4.5;//10
    public static final double ARM_PROX_KI = 0;
    public static final double ARM_PROX_KD = 40;//200
    public static final double ARM_PROX_KF = 0;
    public static final double ARM_PROX_A_FEEDFORWARD = .345;

    public static final double ARM_DIST_KP = 2.4;//7.5
    public static final double ARM_DIST_KI = 0;
    public static final double ARM_DIST_KD = 0;//75
    public static final double ARM_DIST_KF = 0;
    public static final double ARM_DIST_A_FEEDFORWARD = 0; //.16

    /**
     * General Configuration --------------------------------------
     */

    public static final String ROBOT_NAME = "Bah";

    //MP Test mode values
    public static final boolean ENABLE_MP_TEST_MODE = true; //enables motion profiling test across all modes
    public static final double MP_TEST_SPEED = 72; //in /s
    public static final boolean RAMPUP = false;

    //Stick Constants
    public static final Joystick MASTER = new Joystick(0);
    public static final HIDHelper.HIDConstants MASTER_STICK = new HIDHelper.HIDConstants(MASTER, 0.2, 0.99, 0.99, 0.5, 2);

    //Startup Constants
    public static final double LOOPER_DT = 0.01; //dt in seconds
    public static final String DATA_SEPARATOR = ",";
    public static final String[] NUMBER_KEYS = {
            "Drive/Pose/Theta",
            "Drive/Pose/X",
            "Drive/Pose/Y",
            "Drive/Error/Theta",
            "Drive/Error/X",
            "Drive/Error/Y",
            "Drive/Setpoint/Theta",
            "Drive/Setpoint/X",
            "Drive/Setpoint/Y",
            "Drive/Left Demand",
            "Drive/Right Demand",
            "Drive/Left Talon Velocity",
            "Drive/Right Talon Velocity",
            "Drive/Error/Left Talon Error",
            "Drive/Error/Right Talon Error",
            "Drive/Misc/Left FeedForward",
            "Drive/Misc/Right FeedForward",
            "Drive/Left Talon Voltage Out",
            "Drive/Right Talon Voltage Out"
    };
}



