//Application to discover the k-hop neighbors for every node in a distriibuted system

/*Payload message type details:
    1 = do getNeighbors
    2 = array of neighbors
*/

class Application implements Listener
{
    Node myNode;
    NodeID myID;
    
    //Node ids of my neighbors
    NodeID[] neighbors;
    
    //Flag to check if connection to neighbors[i] has been broken
    boolean[] brokenNeighbors;
    
    //flag to indicate that the algorithm is over
    boolean terminating;
    
    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message)
    {
        //Extract payload from message
        Payload p = Payload.getPayload(message.data);
        
        int round = p.round;

        if(round <= numOfRows)
        {
            return;
        }

        if(round == numOfRows)
        {
            if(p.messageType == 1)
        {
            //Messagetype 1 is (do getNeighbors)
            //send oneHopNeighbors to source
			
            Payload newp = new Payload(2, round, oneHopNeighbors);
            Message msg = new Message(myID, newp.toBytes()); 

            myNode.send(msg, message.source);  
        }

        else if(p.messageType == 2)
        //Message type 2 is (array of neighbors)
        {
            NodeID [] rcvdNeighbors = p.oneHopNeighbors;
            //parse for what's needed
            int neighborLength = rcvdNeighbors.length;
            for (i = 0; i < neighborLength; i++)
            {
                if (rcvdNeighbors[i] == myID)
                {
                    //discard
                }
                else if(rcvdNeighbors[i] == already discovered neighbor)
                {
                    //discard
                } 
                else 
                {
                    //write to output file
                }
            }
        }
        }
        
        if(round >= numOfRows)
        {
            buffer;
        }
        
    }
    
    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor)
    {
        for(int i = 0; i < neighbors.length; i++)
        {
            if(neighbor.getID() == neighbors[i].getID())
            {
                brokenNeighbors[i] = true;
                notifyAll();
                if(!terminating)
                {
                    terminating = true;
                    myNode.tearDown();
                }
                return;
            }
        }
    }
    
    //Method called by node  to initate neighbor detection algorithm
    //Synchronized method only releases control on wait or return
    public synchronized void discoverNeighbors()
    {
        //ROUND k
        //send message type 1 to all k-hop neighbors

        int neighborLength = oneHopNeighbors.length;
        for (i = 0; i < neighborLength; i++)
        {
            Payload p = new Payload(1, round, oneHopNeighbors);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(message, oneHopNeighbors[i]);
        }
    }

    String configFile;
    
    //Constructor
    public Application(NodeID identifier, String configFile)
    {
        myID = identifier;
        this.configFile = configFile;
    }
    
    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run()
    {
        //Construct node
        myNode = new Node(myID, configFile, this);
        NodeID [] oneHopNeighbors = myNode.getNeighbors();

        //write neighbors to output file
		String outputData = "1: neighbors[1] neighbors[2]";
		try{
			//Creates FileWriter
			Filewriter output = new FileWriter(String myID, true);
			//Writes the string to the file
			output.write(outputData;)
			//Closes the writer
			output.close();
		}
		catch (Exception e) {
			e.getStackTrace ();
		}

        //numOfRows
        //numOfNodes
        
        //Node initiates neighbor detection
        while (round <= numOfNodes)
        {
            discoverNeighbors();
            round++;
        }
        
        myNode.tearDown();

        for(int i = 0; i < neighbors.length; i++)
        {
            while(!brokenNeighbors[i])
            {
                try
                {
                    //wait till we get a broken reply from each neighbor
                    wait();
                }
                catch(InterruptedException ie)
                {
                }
            }
        }
    }
}

