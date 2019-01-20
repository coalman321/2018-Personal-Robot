using System.IO;
using UnityEngine;

public class GameController
{

    private static readonly GameController instance = new GameController();

    public static GameController getInstance() {
        return instance;
    }

    public string loadedFile { get; set; }
    
    public string SaveLocation { get; }

    public NetworkHelper.Mode mode { get; set; }

    private GameController() {
        SaveLocation = Application.dataPath + "/saves";
        Debug.Log("Save Location set to : " + SaveLocation);
        FileInfo file = new FileInfo(SaveLocation + "/test.sav");
        file.Directory.Create();
        Debug.Log("Directory Established");
    }

}
