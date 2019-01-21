using UnityEngine;

public class RobotDrive : MonoBehaviour
{
    public float conversion;
    public float gravity;
    public int timeToNewFile = 100;
    public string robotIP = "10.41.45.2";
    

    private NetworkHelper net;
    private Rigidbody rb;
    private float xInitialPosition, yInitialPosition, zInitialPosition, yInitialRotation;
    private float x, z, theta;
    private int frame, frames;
    
    // Start is called before the first frame update
    private void Start()
    {
        
        xInitialPosition = gameObject.transform.position.x;
        yInitialPosition = gameObject.transform.position.y;
        zInitialPosition = gameObject.transform.position.z;
        yInitialRotation = gameObject.transform.eulerAngles.y;
        //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", xInitialPosition, zInitialPosition, yInitialRotation));
        Debug.Log(string.Format("Game Controller Mode : {0} \t current File: {1}" , GameController.getInstance().mode, GameController.getInstance().loadedFile));

        net = new NetworkHelper(5800, timeToNewFile, GameController.getInstance().mode, robotIP);
        if (net.mode == NetworkHelper.Mode.Playback) {
            frames = net.loadSave(GameController.getInstance().loadedFile);
            frame = 0;
        }
        
    }

    // Update is called once per frame
    private void Update()
    {
        if (net.mode == NetworkHelper.Mode.Playback) {
            net.update(frame);
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getZ() / conversion;
            theta = yInitialRotation - net.getTheta();
            gameObject.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            gameObject.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", x, y, theta));
        }
        else if (net.mode == NetworkHelper.Mode.Recording || net.mode == NetworkHelper.Mode.Networked){
            net.update(0); // frame index is unused therefore zero
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getZ() / conversion;
            theta = yInitialRotation - net.getTheta();
            gameObject.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            gameObject.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", x, z, theta));
        }
    }

    public float autoPercent() {
        return net.getCurrentState() / net.getTotalStates();
    }

    public int getFrames() {
        return frames;
    }

    public void setFrame(int index)
    {
        frame = index > 0 ? (index < frames? index : frames) : 0;
    }

    public NetworkHelper.Mode getNetworkMode() {
        return net.mode;
    }
}