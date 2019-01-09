package frc.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Constants;

public class VersionData {

    public static void WriteBuildInfoToDashboard() {
        try {
            File version;
            if(RobotBase.isReal()) version = new File(Filesystem.getDeployDirectory(),"version.dat");
            else version = new File(Filesystem.getLaunchDirectory(),"src\\main\\deploy\\version.dat");
            final BufferedReader reader = new BufferedReader(new FileReader(version));
            final String contents = reader.readLine();
            final int versionid = Integer.parseInt(contents.substring(contents.indexOf("=") + 1,contents.indexOf(';')));
            reader.close();
            DriverStation.reportWarning("== Robot Name == " + Constants.ROBOT_NAME + "        |Version ID: " + versionid + "|", false);
        } catch (IOException e) {
            DriverStation.reportError("Failed to read version.dat in deploy directory!", e.getStackTrace());
        }
        final String COMP_MSG = "WARNING THIS IS THE COMPETITION SOFTWARE CONFIG";
        final String PRAC_MSG = "WARNING THIS IS THE PRACTICE SOFTWARE CONFIG";
        DriverStation.reportWarning((Constants.IS_COMP_BOT ? COMP_MSG : PRAC_MSG) + 
        (Constants.ENABLE_MP_TEST_MODE ? "! MP TEST MODE IS ENABLED!" : "!"), false);
    }

}