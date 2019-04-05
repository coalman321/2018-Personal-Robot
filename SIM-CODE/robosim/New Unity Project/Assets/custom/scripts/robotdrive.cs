using UnityEngine;

public class RobotDrive : MonoBehaviour
{
    public float conversion, shoulder, elbow, wrist;
    public int timeToNewFile = 100, port = 5800;
    public string robotIP = "10.41.45.2";
    public GameObject robot, lowerArm, middleArm, upperArm;

    private NetworkHelper net;
    private Rigidbody rb;
    private JointSpring lowerHinge, middleHinge, upperHinge;
    private float xInitialPosition, yInitialPosition, zInitialPosition, yInitialRotation;
    private float x, z, theta;
    private int frame, frames;

    
    // Start is called before the first frame update
    private void Start() {     
        xInitialPosition = robot.transform.position.x;
        yInitialPosition = robot.transform.position.y;
        zInitialPosition = robot.transform.position.z;
        yInitialRotation = robot.transform.eulerAngles.y;
        lowerHinge = lowerArm.GetComponent<HingeJoint>().spring;
        middleHinge = middleArm.GetComponent<HingeJoint>().spring;
        upperHinge = upperArm.GetComponent<HingeJoint>().spring;

        //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", xInitialPosition, zInitialPosition, yInitialRotation));
        Debug.Log(string.Format("Game Controller Mode : {0} \t current File: {1}" , GameController.getInstance().mode, GameController.getInstance().loadedFile));

        net = new NetworkHelper(port, timeToNewFile, GameController.getInstance().mode, robotIP);
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
            robot.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            robot.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", x, y, theta));

            lowerHinge.targetPosition = shoulder;
            lowerArm.GetComponent<HingeJoint>().spring = lowerHinge;
            middleHinge.targetPosition = elbow;
            middleArm.GetComponent<HingeJoint>().spring = middleHinge;
            upperHinge.targetPosition = wrist;
            upperArm.GetComponent<HingeJoint>().spring = upperHinge;

        }
        else if (net.mode == NetworkHelper.Mode.Recording || net.mode == NetworkHelper.Mode.Networked){
            net.update(0); // frame index is unused therefore zero
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getZ() / conversion;
            theta = yInitialRotation - net.getTheta();
            robot.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            robot.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            
            lowerHinge.targetPosition = net.getProx();
            lowerArm.GetComponent<HingeJoint>().spring = lowerHinge;
            middleHinge.targetPosition = net.getDist();
            middleArm.GetComponent<HingeJoint>().spring = middleHinge;
            upperHinge.targetPosition = net.getWrist();
            upperArm.GetComponent<HingeJoint>().spring = upperHinge;
            
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