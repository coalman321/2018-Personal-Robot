using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class SaveButtonController : MonoBehaviour {
    
    public Text fileName;
    public string file;

    public void loadSave(int sceneIndex) {
       GameController.getInstance().loadedFile = file;
       GameController.getInstance().mode = NetworkHelper.Mode.Playback;
       SceneManager.LoadScene(sceneIndex); 
    }
       
}
