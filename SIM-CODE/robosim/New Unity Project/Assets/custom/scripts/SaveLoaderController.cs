using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SaveLoaderController : MonoBehaviour {
    public GameObject loadButonPrefab, contentPanel;

    // Start is called before the first frame update
    void Start() {
        string[] saves = NetworkHelper.getSaves();
        foreach (string save in saves) {
            GameObject newLoadableSave = Instantiate(loadButonPrefab);
            SaveButtonController controller = newLoadableSave.GetComponent<SaveButtonController>();
            controller.fileName.text = save;
            newLoadableSave.transform.parent = contentPanel.transform;
            newLoadableSave.transform.localScale = Vector3.one;
        }
    }
}
