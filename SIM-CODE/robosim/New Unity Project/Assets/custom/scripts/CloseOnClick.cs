using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CloseOnClick : MonoBehaviour
{
    public void doClose()
    {
        #if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
        #else
            Application.Quit();
        #endif
    }

    public void Update() {
        if(Input.GetKeyDown(KeyCode.Escape)) doClose();
    }
}
