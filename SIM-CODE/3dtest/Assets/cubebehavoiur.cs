
using UnityEngine;

public class cubebehavoiur : MonoBehaviour {

	// Use this for initialization
	private float speed = 50f;
	
	void Start ()
	{
		Debug.Log("hello there");
		
	}
	
	// Update is called once per frame
	void Update () {
		if (Input.GetKey("a"))
		{
			transform.Rotate(new Vector3(0,1,0), speed * Time.deltaTime);
			return;
		}
		if(Input.GetKey("w"))
		{
			transform.Rotate(new Vector3(-1,0,0), speed * Time.deltaTime);
			return;
		}

		if (Input.GetKey("c"))
		{
			
		}
	}

}
