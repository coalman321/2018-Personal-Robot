using System.Diagnostics;
using System.Timers;
using UnityEngine;
using Debug = UnityEngine.Debug;

public class PlayerController : MonoBehaviour
{
    
    public float speed;
    public bool enableDebug;

    private Rigidbody rb;
    
    private ReadOptimizedSocketTables tables;
    private Stopwatch watch = new Stopwatch();
    private string hi = "";
    private double ten = 0;
    
    // Start is called before the first frame update
    void Start()
    {
        tables = new ReadOptimizedSocketTables("127.0.0.1", enableDebug);
        rb = GetComponent<Rigidbody>();
        tables.startUpdates();
        tables.putNumber("vert", 0);
        tables.putNumber("horiz", 0);
    }

    // Update is called once per frame
    void Update()
    {
        float moveHorizontal = Input.GetAxis ("Horizontal");
        float moveVertical = Input.GetAxis ("Vertical");
        
        //Debug.Log("updating vert & horiz");
        tables.putNumber("vert", moveVertical);
        tables.putNumber("horiz", moveHorizontal);
        //Debug.Log("updates complete");

        Vector3 movement = new Vector3 (moveHorizontal, 0.0f, moveVertical);

        rb.AddForce (movement * speed);

        //Debug.Log($" hi : {hi}    ten : {ten}    @ { (double)watch.ElapsedTicks / (double)Stopwatch.Frequency}");
    }

    void OnApplicationQuit() {
        tables.stopUpdates();
    }
}

