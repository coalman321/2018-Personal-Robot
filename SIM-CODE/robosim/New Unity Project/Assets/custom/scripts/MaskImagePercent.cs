using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MaskImagePercent : MonoBehaviour
{

    public float percent;
    public int max;
    public GameObject obj;

    // Update is called once per frame
    void Update()
    {
        RectTransform trans = obj.GetComponent<RectTransform>();
        percent = percent < 0 ? 0 : percent;
        percent = percent > 1 ? 1 : percent;
        trans.sizeDelta = new Vector2(max * percent, 7);
    }

    void setPercent(float percent) => this.percent = percent;
}
