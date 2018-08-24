package frc.lib.util;

/**
 * A drivetrain command consisting of the linear and angular motor settings and whether the brake mode is enabled.
 */
public class DriveSignal {
    protected double mLinearMotor;
    protected double mAngularMotor;
    protected boolean mBrakeMode;

    public DriveSignal(double linear, double angular) {
        this(linear, angular, false);
    }

    public DriveSignal(double linear, double angular, boolean brakeMode) {
        mLinearMotor = linear;
        mAngularMotor = angular;
        mBrakeMode = brakeMode;
    }

    public static final DriveSignal NEUTRAL = new DriveSignal(0, 0);
    public static final DriveSignal BRAKE = new DriveSignal(0, 0, true);

    public double getLinear() {
        return mLinearMotor;
    }

    public double getAngular() {
        return mAngularMotor;
    }

    public boolean getBrakeMode() {
        return mBrakeMode;
    }

    @Override
    public String toString() {
        return "L: " + mLinearMotor + ", R: " + mAngularMotor + (mBrakeMode ? ", BRAKE" : "");
    }
}