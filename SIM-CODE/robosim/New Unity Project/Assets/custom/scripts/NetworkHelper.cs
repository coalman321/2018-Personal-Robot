using System.Net;
using System.Net.Sockets;
using System.Text;

public class NetworkHelper
{
    private readonly UdpClient listener;
    private string[] data;
    private IPEndPoint groupEP;
    private byte[] raw;
    private string s;

    // Start is called before the first frame update
    public NetworkHelper(int port)
    {
        listener = new UdpClient(port);
        groupEP = new IPEndPoint(IPAddress.Any, port);
        data = new[] {"0", "0", "0", "0", "0", "0", "0", "0", "0"};
    }

    public float getTimeStamp()
    {
        return float.Parse(data[0]);
    }

    public float getX()
    {
        return float.Parse(data[1]);
    }

    public float getY()
    {
        return float.Parse(data[2]);
    }

    public float getTheta()
    {
        return float.Parse(data[3]);
    }

    public float getProx()
    {
        return float.Parse(data[4]);
    }

    public float getDist()
    {
        return float.Parse(data[5]);
    }

    public float getWrist()
    {
        return float.Parse(data[6]);
    }

    public int getCurrentState()
    {
        return int.Parse(data[7]);
    }

    public int getTotalStates()
    {
        return int.Parse(data[8]);
    }

    public void update()
    {
        if (listener.Available > 0)
        {
            while (listener.Available > 1)
            {
                //void unused packets
                listener.Receive(ref groupEP);
            }

            raw = listener.Receive(ref groupEP);
            s = Encoding.ASCII.GetString(raw);
            data = CSVReader.readCSVLine(s);
            //Debug.Log(data[0] + " " + data[1]);
        }
    }
}