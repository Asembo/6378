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

    //node ID array of all hop neighbors
    NodeID[] allHopNeighbors = null;

    //length of array of all hop neihgbors to be checked to avoid repeating nodes
    int allHopLength = 0;

    //Flag to check if connection to neighbors[i] has been broken
    boolean[] brokenNeighbors;
    
    //flag to indicate that the algorithm is over
    boolean terminating;
    
    //number of rows in output file used to indicate what round is being executed
    int numOfRows = 0;

    //number of nodes in the distributed system
    int numOfNodes = 0;

    //round ID indicating current round
    int myRound = 0;

    //length of one-hop neighbor array
    int neighborLength = 0;
    
    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message)
    {
        //Extract payload from message
        Payload p = Payload.getPayload(message.data);
        
        int messageRound = p.roundHopID;

        if(messageRound < myRound)
        {
            return;
        }
        if(messageRound > myRound)
        {
            //buffer
            while(myRound < messageRound)
            {
                wait();
            }
        }

        else if(messageRound == myRound)
        {
            if(p.messageType == 1)
            {
                //Messagetype 1 is (do getNeighbors)
                //send oneHopNeighbors to source
			
                Payload newp = new Payload(2, myRound, oneHopNeighbors);
                Message msg = new Message(myID, newp.toBytes()); 
                myNode.send(msg, message.source);  
            }

            else if(p.messageType == 2)
            //Message type 2 is (array of neighbors)
            {
                NodeID [] rcvdNeighbors;
                neighborLength = (p.oneHopNeighbors).length;
                allHopLength = allHopNeighbors.length;
                for(i = 0; i < neighborLength; i++)
                {
                    rcvdNeighbors[i] = p.oneHopNeighbors[i];
                    allHopNeighbors[i + allHopLength] = (myNode.getNeighbors())[i];
                }

                int rcvdNeighborLength = rcvdNeighbors.length;
                //parse for what's needed
                for(int i = 0; i < rcvdNeighborLength; i++)
                {
                    for(int j = 0; j < allHopLength; j++)
                    {
                        if (!(rcvdNeighbors[i] == myID) || (rcvdNeighbors[i] == allHopNeighbors[j]))
                        {
                            //write to output file
                            myRound++;
                        }
                    }

                }   
            }   
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
        //send message type 1 to all k-hop neighbors
        for (int i = 0; i < neighborLength; i++)
        {
            Payload p = new Payload(1, myRound, oneHopNeighbors);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, oneHopNeighbors[i]);
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
        NodeID [] oneHopNeighbors = null;
        neighborLength = (myNode.getNeighbors()).length;
        for(int i = 0; i < neighborLength; i++)
        {
            oneHopNeighbors[i] = (myNode.getNeighbors())[i];
            allHopNeighbors[i] = (myNode.getNeighbors())[i];
        }

        //write neighbors to output file
        
        myRound++;
        //numOfNodes
        
        //Node initiates neighbor detection
        while (myRound <= numOfNodes)
        {
            discoverNeighbors();
            myRound++;
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

