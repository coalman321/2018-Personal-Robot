package frc.robot.lib;

import frc.robot.Constants;

public class Util {

    public static final double kEpsilon = 1e-12;

    private Util() {
    }

    public static boolean epsilonEquals(double a, double b, double epsilon) { return (a - epsilon <= b) && (a + epsilon >= b); }

    public static boolean epsilonEquals(double a, double b) {
        return epsilonEquals(a, b, kEpsilon);
    }

    public static boolean epsilonEquals(int a, int b, int epsilon) {
        return (a - epsilon <= b) && (a + epsilon >= b);
    }

    public static double inchesToRotations(double inches) {
        return inches / (Constants.WHEEL_DIAMETER * Math.PI);
    }

    public static double inchesPerSecondToRpm(double inches_per_second) { return inchesToRotations(inches_per_second) * 60; }

    public static double uPer100MsToRPM(double uPer100Ms){
        return (uPer100Ms* 75) / 512.0;
    }

    public static double RPMToUnitsPer100Ms(double RPM){
        return (RPM * 512) / 75.0;
    }

}
