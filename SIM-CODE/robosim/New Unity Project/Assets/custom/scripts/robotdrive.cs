using System;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

using UnityEngine;

public class robotdrive : MonoBehaviour
{
    public const int port = 5800;

    public float speed;
    private Rigidbody rb;
    private UdpClient listener;
    private IPEndPoint groupEP;
    private byte[] data = new byte[100];
    private double pose_x = 0, pose_y = 0, pose_theta = 0;
    private int arm_prox = 0, arm_dist = 0, arm_wrist = 0;
    
    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody>();
        listener = new UdpClient(port);
        groupEP = new IPEndPoint(IPAddress.Any, port);
        print(BitConverter.GetBytes(100.1098532).Length);
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            listener.Close();
            Application.Quit();
        }
        if (listener.Available > 0)
        {
            data = listener.Receive(ref groupEP);
            //decode
        }
        float horiz = -Input.GetAxis("Vertical");
        float vert = Input.GetAxis("Horizontal");
        Vector3 move = new Vector3(horiz, 0.0f, vert);
        rb.AddForce(move * speed);
    }
    
    
}
