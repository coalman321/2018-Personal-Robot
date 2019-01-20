using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class SaveButtonController : MonoBehaviour {
    
       public Text fileName;

       public void loadSave(int sceneIndex) {
           GameController.getInstance().loadedFile = fileName.text;
           SceneManager.LoadScene(sceneIndex); 
       }
       
}
