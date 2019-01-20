using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ShowFileSelectionMenu : MonoBehaviour {

    public GameObject toHide, toShow;
    // Start is called before the first frame update
    public void onClick() {
        toHide.SetActive(false);
        toShow.SetActive(true);
    }
}
