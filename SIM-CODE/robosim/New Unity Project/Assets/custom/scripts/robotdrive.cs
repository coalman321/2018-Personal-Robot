using System;
using UnityEngine;

public class robotdrive : MonoBehaviour
{
    public float speed;
    public float gravity;
    public float conversion;
    public GameObject obj;
    public bool isNetworked;
    
    private Rigidbody rb;
    private NetworkHelper net;
    private Transform startingPose;
    private float x, y, theta;
    
    // Start is called before the first frame update
    void Start() {
        startingPose = obj.transform;
        if(!isNetworked)rb = GetComponent<Rigidbody>();
        net = new NetworkHelper(5800);
    }

    // Update is called once per frame
    void Update()
    {
        if (isNetworked) {
            net.update();
            x = net.getX() / conversion - startingPose.position.x;
            y = net.getY() / conversion - startingPose.position.z;
            theta = net.getTheta() - startingPose.rotation.y;
            obj.transform.position = new Vector3(x, startingPose.position.y, y); // y is vertical in unity but not in pose
            obj.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            Debug.Log(String.Format("X: {1}, Y: {2}, Theta: {3}", net.getX(), net.getY(), net.getTheta()));

        }
        else{
            float horiz = -Input.GetAxis("Vertical");
            float vert = Input.GetAxis("Horizontal");
            Vector3 move = new Vector3(horiz, gravity / speed, vert);
            rb.AddForce(move * Time.deltaTime * speed);
        }
    }

    public NetworkHelper getNetworkHelper() {
        return net;
    }

    public bool getNetworked() {
        return isNetworked;
    }
    
    
}
