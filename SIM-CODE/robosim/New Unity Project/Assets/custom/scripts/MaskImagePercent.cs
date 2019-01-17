using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MaskImagePercent : MonoBehaviour
{

    public float percent;
    public int max;
    public GameObject obj;
    public robotdrive drive;
    
    private bool isNetworked;
    private NetworkHelper helper;

    // Update is called once per frame
    void Start() {
        helper = drive.getNetworkHelper();
        isNetworked = drive.getNetworked();
    }
    
    void Update() {
        isNetworked = drive.getNetworked();
        if (isNetworked) percent = (float)helper.getCurrentState() / helper.getTotalStates();
        
        RectTransform trans = obj.GetComponent<RectTransform>();
        percent = percent < 0 ? 0 : percent;
        percent = percent > 1 ? 1 : percent;
        trans.sizeDelta = new Vector2(max * percent, 7);
    }

    void setPercent(float percent) => this.percent = percent;
}
