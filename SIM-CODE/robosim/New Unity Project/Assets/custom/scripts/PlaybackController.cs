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
        if (AllowPlayback && frame < drive.getFrames()) {
            AllowEvent = false;
            drive.setFrame(frame++);
            slider.value = (float)frame / drive.getFrames();
        }
        else {
            frame = (int)(slider.value * drive.getFrames());
            Debug.Log("sweeping timeline to: " + frame);
            drive.setFrame(frame);
        }

        AllowEvent = true;
    }

    public void setPlayback(bool playback) {
        AllowPlayback = AllowEvent? playback : AllowPlayback;
    }
}
