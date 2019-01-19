using System.Runtime.ConstrainedExecution;
using UnityEngine;

public class robotdrive : MonoBehaviour
{
    public float conversion;
    public float gravity;
    public bool isNetworked;
    private NetworkHelper net;

    private Rigidbody rb;
    public float speed;
    private float xInitialPosition, yInitialPosition, zInitialPosition, yInitialRotation;
    private float x, z, theta;

    // Start is called before the first frame update
    private void Start()
    {
        net = new NetworkHelper(5800);
        xInitialPosition = gameObject.transform.position.x;
        yInitialPosition = gameObject.transform.position.y;
        zInitialPosition = gameObject.transform.position.z;
        yInitialRotation = gameObject.transform.rotation.y;
        Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", xInitialPosition, yInitialPosition, yInitialRotation));
        if (!isNetworked) rb = GetComponent<Rigidbody>();
    }

    // Update is called once per frame
    private void Update()
    {
        if (isNetworked)
        {
            net.update();
            x = xInitialPosition - net.getX() / conversion;
            z = zInitialPosition - net.getY() / conversion;
            theta = yInitialRotation - net.getTheta();
            gameObject.transform.position = new Vector3(x, yInitialPosition, z); // y is vertical in unity but not in pose
            gameObject.transform.rotation = Quaternion.Euler(0.0f, theta, 0.0f); //rotation around vertical axis
            //Debug.Log(string.Format("X: {0}, Y: {1}, Theta: {2}", x, y, theta));
        }
        else
        {
            var horiz = -Input.GetAxis("Vertical");
            var vert = Input.GetAxis("Horizontal");
            var move = new Vector3(horiz, gravity / speed, vert);
            rb.AddForce(move * Time.deltaTime * speed);
        }
    }

    public NetworkHelper getNetworkHelper()
    {
        return net;
    }
}