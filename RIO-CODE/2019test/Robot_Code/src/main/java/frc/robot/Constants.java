package frc.robot;

public class Constants{

    /*
     * ----------------------
     * || Port Assignments ||
     * ----------------------
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

    public static final String DATA_SEPERATOR = ","; //logging data file delimiter
    public static final String[] NUMBER_KEYS = {};
    public static final String[] STRING_KEYS = {}; //no string keys currently

    public static final double LOOPER_DT = 0.010;
    public static final String ROBOT_NAME = "durrr";
    public static final boolean IS_COMP_BOT = true;
    public static final boolean ENABLE_MP_TEST_MODE = false;

}