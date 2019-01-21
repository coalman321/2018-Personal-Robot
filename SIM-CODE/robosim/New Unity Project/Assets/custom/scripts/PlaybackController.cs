using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PlaybackController : MonoBehaviour {
    public RobotDrive drive;
    public Slider slider;

    private int frame;
    private bool AllowPlayback, AllowEvent;

    void Start() {
        frame = 0;
    }
    
    // Update is called once per frame
    void Update() {
        if (AllowPlayback && frame < drive.frames) {
            AllowEvent = false;
            drive.frame = frame++;
            slider.value = (float)frame / drive.frames;
        }
        else {
            frame = (int)(slider.value * drive.frames);
            Debug.Log("sweeping timeline to: " + frame);
            drive.frame = frame;
        }

        AllowEvent = true;
    }

    public void setPlayback(bool playback) {
        AllowPlayback = AllowEvent? playback : AllowPlayback;
    }
}
