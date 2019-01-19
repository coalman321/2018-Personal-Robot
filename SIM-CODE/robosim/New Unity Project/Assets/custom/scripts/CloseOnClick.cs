using UnityEditor;
using UnityEngine;

public class CloseOnClick : MonoBehaviour
{
    public void doClose()
    {
#if UNITY_EDITOR
        EditorApplication.isPlaying = false;
#else
            Application.Quit();
        #endif
    }

    public void Update()
    {
        if (Input.GetKeyDown(KeyCode.Escape)) doClose();
    }
}