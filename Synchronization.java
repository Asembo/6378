//Synchronization Layer


/*Payload message type details:
	1 = request message
	2 = reply message
*/


public class DLock implements Listener
{

    // implementation-specific private data as needed

    boolean executingCS = false;
    int key;
    NodeID myNode, receipient;
    NodeID [] neighbors;
    Map<Integer, NodeID> map = new HashMap<>();

    //synchronized receive
	//invoked by Node class when it receives a message
	public synchronized void receive(Message message)
	{
		//Extract payload from message
		Payload p = Payload.getPayload(message.data);
		
		if(p.messageType == 1)
		{
			//Messagetype 1 is request message

            if(p.timestamp > timestamp || executingCS == true)
            {
            //defer 
            map.put(key, message.source);
            key++;
            System.out.println("Buffering message....");
            }

            else if(p.timestamp < timestamp)
            {
            //send reply
            Payload p = new Payload(2, timestamp);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, message.source);
            }

            else 
            {
            //send reply
            Payload p = new Payload(2, timestamp);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, message.source);
            }
        }
		else if(p.messageType == 2)
		{
			//Messagetype 2 is reply message
            rcvdReplies++;
		}
	}


// public constructor
public Dlock( NodeID identifier, String configFileName );

//Construct node
myNode = new Node(myID, configFile, this);
neighbors = myNode.getNeighbors();

// public methods
public void lock( )
{
    rcvdReplies = 0;
    key = 1;

    Payload p = new Payload(1, timestamp);
    Message msg = new Message(myID, p.toBytes());
    myNode.sendToAll(msg);

    while(rcvdReplies < neighbors.length - 1)
    {
        try
        {
            //wait to receive messages from neighbors
            wait();
        }
        catch(InterruptedException ie)
        {
        }
    }
        executingCS = true;
        return;
}
public void unlock( )
{
    executingCS = false;
    for(int i = 0; i < key; i++)
    {
    if(map.get(key) != null)
        {
            receipient = map.get(key);
            Payload p = new Payload(2, timestamp);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, receipient);
        }
    }
}


// implementation specific private methods as needed

}
