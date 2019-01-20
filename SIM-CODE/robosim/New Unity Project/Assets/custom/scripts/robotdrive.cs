using System.Runtime.ConstrainedExecution;
using UnityEngine;

public class robotdrive : MonoBehaviour
{
    public float conversion;
    public float gravity;
    public bool isNetworked, isRecorded, isPlayback;
    public string saveDir, file;
    
    private NetworkHelper net;
    private Rigidbody rb;
    public float speed;
    private float xInitialPosition, yInitialPosition, zInitialPosition, yInitialRotation;
    private float x, z, theta;
    private int frame, frames;

    public int Frame {
        get { return frame; }
        set { frame = value; }
    }
    
    // Start is called before the first frame update
    private void Start()
    {
        net = new NetworkHelper(5800, saveDir, 100, NetworkHelper.Mode.Networked);// start in networked mode and adjust as needed
        xInitialPosition = gameObject.transform.position.x;
        yInitialPosition = gameObject.transform.position.y;
        zInitialPosition = gameObject.transform.position.z;
        yInitialRotation = gameObject.transform.rotation.y;
        //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", xInitialPosition, yInitialPosition, yInitialRotation));
        
        if (isNetworked) net.mode = NetworkHelper.Mode.Networked;
        else if (isRecorded) net.mode = NetworkHelper.Mode.Recording;
        else if (isPlayback) {
            net.mode = NetworkHelper.Mode.Playback;
            frames = net.loadSave(saveDir + "\\" + file);
            frame = 0;
        }
    }

    // Update is called once per frame
    private void Update()
    {
        if (net.mode == NetworkHelper.Mode.Playback) {
            net.update(frame);
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getY() / conversion;
            theta = yInitialRotation - net.getTheta();
            gameObject.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            gameObject.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            frame ++;
        }
        else if (net.mode == NetworkHelper.Mode.Recording || net.mode == NetworkHelper.Mode.Networked)
        {
            net.update(0); // frame index is unused therefore zero
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getY() / conversion;
            theta = yInitialRotation - net.getTheta();
            gameObject.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            gameObject.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", x, y, theta));
        }
    }

    public NetworkHelper getNetworkHelper()
    {
        return net;
    }
}