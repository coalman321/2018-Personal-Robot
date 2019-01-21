using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SetPlaybackController : MonoBehaviour {
    public PlaybackController PlaybackController;
    public bool PlaybackState;
    
    public void onClick() {
        PlaybackController.setPlayback(PlaybackState);
    }
}
