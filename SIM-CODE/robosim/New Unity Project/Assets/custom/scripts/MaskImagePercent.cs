using UnityEngine;

public class MaskImagePercent : MonoBehaviour
{
    public robotdrive drive;

    private bool isNetworked;
    public int max;
    public GameObject obj;

    public float percent;



    private void Update() {
        if (drive.getNetworkMode() != NetworkHelper.Mode.Disconnected) percent = drive.autoPercent();

        var trans = obj.GetComponent<RectTransform>();
        percent = percent < 0 ? 0 : percent;
        percent = percent > 1 ? 1 : percent;
        trans.sizeDelta = new Vector2(max * percent, 7);
    }

}