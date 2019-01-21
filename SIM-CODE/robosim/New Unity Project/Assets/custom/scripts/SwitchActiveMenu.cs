using UnityEngine;

public class SwitchActiveMenu : MonoBehaviour {

    public GameObject toHide, toShow;

    public void onClick() {
        toHide.SetActive(false);
        toShow.SetActive(true);
    }
}
