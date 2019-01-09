package frc.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Constants;

public class VersionData {

    public static void WriteBuildInfoToDashboard() {
        DriverStation.reportWarning("== Robot Name == " + Constants.ROBOT_NAME + "        |Version ID: " + getInfo("VERSION_ID") + "|", false);
        final String COMP_MSG = "WARNING THIS IS THE COMPETITION SOFTWARE CONFIG";
        final String PRAC_MSG = "WARNING THIS IS THE PRACTICE SOFTWARE CONFIG";
        DriverStation.reportWarning((Constants.IS_COMP_BOT ? COMP_MSG : PRAC_MSG) + 
        (Constants.ENABLE_MP_TEST_MODE ? "! MP TEST MODE IS ENABLED!" : "!"), false);
    }

    public static String getInfo(String key){
        try {
            File version;
            if(RobotBase.isReal()) version = new File(Filesystem.getDeployDirectory(),"version.dat");
            else version = new File(Filesystem.getLaunchDirectory(),"src\\main\\deploy\\version.dat");
            List<String> lines = Files.readAllLines(version.toPath());
            int i = 0;
            int equalsInd = 0;
            for(String line: lines){
                if(line.indexOf(key) > 0){
                    i = line.indexOf(key) + key.length();
                    while(line.charAt(i) != '='){
                        i++;
                    }
                    equalsInd = i;
                    while(line.charAt(i) != ';'){
                        i++;
                    }
                    return line.substring(equalsInd, i);
                }
            }
            DriverStation.reportWarning("Failed to discover " + key + "in Version.dat", false);
            return "";
        } catch (Exception e) {
            DriverStation.reportError("Failed to read version.dat in deploy directory!", e.getStackTrace());
            return "";
        }
    }

}