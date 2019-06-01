using UnityEngine;
using UnityEngine.SceneManagement;

public class LoadSceneOnClick : MonoBehaviour
{
    public void LoadByIndex(int sceneIndex)
    {
        GameController.getInstance().mode = NetworkHelper.Mode.Recording;
        SceneManager.LoadScene(sceneIndex);
    }
    
}