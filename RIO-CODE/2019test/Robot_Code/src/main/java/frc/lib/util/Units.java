package frc.lib.util;

import frc.robot.Constants;

public class Units {
    public static double rpm_to_rads_per_sec(double rpm) {
        return rpm * 2.0 * Math.PI / 60.0;
    }

    public static double rads_per_sec_to_rpm(double rads_per_sec) {
        return rads_per_sec * 60.0 / (2.0 * Math.PI);
    }

    public static double inches_to_meters(double inches) {
        return inches * 0.0254;
    }

    public static double meters_to_inches(double meters) {
        return meters / 0.0254;
    }

    public static double feet_to_meters(double feet) {
        return inches_to_meters(feet * 12.0);
    }

    public static double meters_to_feet(double meters) {
        return meters_to_inches(meters) / 12.0;
    }

    public static double degrees_to_radians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double radians_to_degrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static double inchesToRotations(double inches) { return inches / (Constants.DRIVE_WHEEL_DIAMETER_INCHES * Math.PI); }

    public static double rotationsToInches(double rotations) { return rotations * (Constants.DRIVE_WHEEL_DIAMETER_INCHES * Math.PI); }

    public static double inchesPerSecondToRpm(double inches_per_second) { return inchesToRotations(inches_per_second) * 60; }

    public static double uPer100MsToRPM(double uPer100Ms) {
        return (uPer100Ms * 75) / 512.0;
    }

    public static double RPMToUnitsPer100Ms(double RPM) {
        return (RPM * 512) / 75.0;
    }

    public static double radiansPerSecondToTicksPer100ms(double rads_per_sec){
        return RPMToUnitsPer100Ms(rads_per_sec_to_rpm(rads_per_sec));
    }
}
