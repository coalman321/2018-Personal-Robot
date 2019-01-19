using UnityEngine;

public class MaskImagePercent : MonoBehaviour
{
    public robotdrive drive;
    private NetworkHelper helper;

    private bool isNetworked;
    public int max;
    public GameObject obj;

    public float percent;

    // Update is called once per frame
    private void Start()
    {
        //helper = drive.getNetworkHelper();
        isNetworked = drive.isNetworked;
    }

    private void Update()
    {
        isNetworked = drive.isNetworked;
        //if (isNetworked) percent = (float)helper.getCurrentState() / helper.getTotalStates();

        var trans = obj.GetComponent<RectTransform>();
        percent = percent < 0 ? 0 : percent;
        percent = percent > 1 ? 1 : percent;
        trans.sizeDelta = new Vector2(max * percent, 7);
    }

    private void setPercent(float percent)
    {
        this.percent = percent;
    }
}