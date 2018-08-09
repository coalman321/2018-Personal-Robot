package frc.lib.util;

import edu.wpi.first.wpilibj.Joystick;

public class HIDHelper {

    public static double[] getAdjStick(HIDConstants constants) {
        double[] out = new double[3];
        out[0] = applyScalarCut(constants.joystick.getX(), constants.deadBand, constants.scalarCutX, constants.polyFunct);
        out[1] = applyScalarCut(constants.joystick.getX(), constants.deadBand, constants.scalarCutY, constants.polyFunct);
        out[2] = applyScalarCut(constants.joystick.getX(), constants.deadBand, constants.scalarCutZ, constants.polyFunct);
        return out;
    }

    private static double applyScalarCut(double stickInput, double deadBand, double scalarCut, int pow){
        return evalDeadBand(stickInput,deadBand,pow) * scalarCut;
    }

    // figures out if the stick value is within the deadband
    private static double evalDeadBand(double stickInpt, double deadBand, int pow) {
        if (Math.abs(stickInpt) < deadBand) {
            return 0;
        } else {
            if (stickInpt < 0) {
                return (0 - Math.pow(stickInpt, pow));
            } else {
                return Math.pow(stickInpt, pow);
            }
        }
    }

    public static class HIDConstants{

        private Joystick joystick;
        private double deadBand, scalarCutX, scalarCutY, scalarCutZ;
        private int polyFunct;

        public HIDConstants(Joystick joystick, double deadBand, double scalarCutX, double scalarCutY, double scalarCutZ, int polyFunct){
            this.joystick = joystick;
            this.deadBand =  deadBand;
            this.scalarCutX = scalarCutX;
            this.scalarCutY = scalarCutY;
            this.scalarCutZ = scalarCutZ;
            this.polyFunct = polyFunct;
        }
    }

}
