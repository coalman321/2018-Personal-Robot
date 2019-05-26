using System.Diagnostics;
using System.Timers;
using UnityEngine;
using Debug = UnityEngine.Debug;

public class PlayerController : MonoBehaviour
{
    
    public float speed;

    private Rigidbody rb;
    
    private ReadOptimizedSocketTables tables = new ReadOptimizedSocketTables("127.0.0.1", true);
    private Stopwatch watch = new Stopwatch();
    private string hi = "";
    private double ten = 0;
    
    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody>();
        tables.startUpdates();
        tables.putString("hi", "hewow");
        tables.putNumber("ten", 10);
        tables.putNumber("vert", 0);
        tables.putNumber("horiz", 0);
    }

    // Update is called once per frame
    void Update()
    {
        float moveHorizontal = Input.GetAxis ("Horizontal");
        float moveVertical = Input.GetAxis ("Vertical");
        
        tables.putNumber("vert", moveVertical);
        tables.putNumber("horiz", moveHorizontal);

        Vector3 movement = new Vector3 (moveHorizontal, 0.0f, moveVertical);

        rb.AddForce (movement * speed);



        watch.Reset();
        watch.Start();
        
        hi = tables.getString("hi", "");
        ten = tables.getNumber("ten", 0);
        
        watch.Stop();
        
        Debug.Log($" hi : {hi}    ten : {ten}    @ { (double)watch.ElapsedTicks / (double)Stopwatch.Frequency}");
    }

    void OnApplicationQuit() {
        tables.stopUpdates();
    }
}

