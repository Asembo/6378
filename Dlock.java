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
    ArrayList<NodeID> deferrals = new ArrayList<NodeID>(neighborLength);

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

                deferrals.add(message.source);

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
        if(rcvdReplies == neighborLength - 1)
        {
            notifyAll();
        } 
	}


    // public constructor
    public Dlock( NodeID identifier, String configFileName )
    {
        myID = identifier;
        this.configFile = configFileName;
    }

    //Construct node
    myNode = new Node(myID, configFile, this);
    neighbors = myNode.getNeighbors();
    neighborLength = neighbors.length;

    // public methods
    public void lock( )
    {
        rcvdReplies = 0;
        
        Payload p = new Payload(1, timestamp);
        Message msg = new Message(myID, p.toBytes());
        myNode.sendToAll(msg);

        while(rcvdReplies < neighborLength)
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
        for (NodeID receipient : arrlist) 
        {
            System.out.println("Number = " + number);
            Payload p = new Payload(2, timestamp);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, receipient);
        }
    }
    //implementation specific private methods as needed 
}
