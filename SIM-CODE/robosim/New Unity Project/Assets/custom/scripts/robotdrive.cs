using System;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

using UnityEngine;
using Quaternion = System.Numerics.Quaternion;

public class robotdrive : MonoBehaviour
{
    public float speed;
    public float gravity;

    public GameObject obj;
    public bool isNetworked;
    private Rigidbody rb;

    private NetworkHelper net;
    
    // Start is called before the first frame update
    void Start()
    {
        
        if(!isNetworked)rb = GetComponent<Rigidbody>();
        net = new NetworkHelper(5800);
    }

    // Update is called once per frame
    void Update()
    {
        if (!isNetworked) {
            float horiz = -Input.GetAxis("Vertical");
            float vert = Input.GetAxis("Horizontal");
            Vector3 move = new Vector3(horiz, gravity / speed, vert);
            rb.AddForce(move * Time.deltaTime * speed);
        }
        else
        {
            net.update();
            obj.transform.position = new Vector3(net.getX(), 3000, net.getY()); //430
            Debug.Log(net.getTheta());
            obj.transform.rotation = UnityEngine.Quaternion.Euler(0.0f, net.getTheta(), 0.0f);
        }
    }
    
    
}
