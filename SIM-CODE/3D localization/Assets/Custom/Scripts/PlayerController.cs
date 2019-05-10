using UnityEngine;

public class PlayerController : MonoBehaviour
{
    
    public float speed;

    private Rigidbody rb;
    
    private SocketTables tables = new SocketTables("127.0.0.1", true);
    
    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody>();
        tables.putString("hi", "hewow");
        tables.putNumber("ten", 10);
    }

    // Update is called once per frame
    void Update()
    {
        float moveHorizontal = Input.GetAxis ("Horizontal");
        float moveVertical = Input.GetAxis ("Vertical");

        Vector3 movement = new Vector3 (moveHorizontal, 0.0f, moveVertical);

        rb.AddForce (movement * speed);

        tables.getString("hi", "");
        //tables.getNumber("ten", 0);
        tables.getAll();
    }
}
