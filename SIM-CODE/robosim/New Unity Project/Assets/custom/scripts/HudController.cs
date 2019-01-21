using UnityEngine;

public class HudController : MonoBehaviour
{
    
    public GameObject ToggleOnKeyPress, PlaybackOnlyElement;
    public string key;

    void Start() {
        if (GameController.getInstance().mode == NetworkHelper.Mode.Playback) {
            PlaybackOnlyElement.SetActive(true);
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(key)) {
            ToggleOnKeyPress.SetActive(!ToggleOnKeyPress.activeSelf);
        }
    }
}
