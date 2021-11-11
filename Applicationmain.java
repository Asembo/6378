//Application to discover the k-hop neighbors for every node in a distriibuted system

/*Payload message type details:
    1 = do getNeighbors
    2 = array of neighbors
*/
import java.io.*;
import java.util.*;
class Application implements Listener
{
    Node myNode;
    NodeID myID;
    
    //Node ids of my neighbors
    NodeID[] oneHopNeighbors;

    //node ID array of all hop neighbors
    NodeID[] allHopNeighbors;

    //length of array of all hop neihgbors to be checked to avoid repeating nodes
    int allHopLength;

    //Flag to check if connection to neighbors[i] has been broken
    boolean[] brokenNeighbors;
    
    //flag to indicate that the algorithm is over
    boolean terminating;

    //number of nodes in the distributed system
    int numOfNodes;

    //round ID indicating current round
    int myRound;

    //length of one-hop neighbor array
    int neighborLength;

    //number of messages received per round (should be 2 per round)
    int rcvdMessages;

    NodeID [] rcvdNeighbors;
    
    Map<Integer, Message> map = new HashMap<>();
    String outputFileName;
	File outputFile;
	PrintStream stream;

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
            map.put(messageRound, message);
            System.out.println("Buffering message....");
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
                rcvdMessages++;
                System.out.println("Message 1 was received from " + (message.source).getID());
            }

            else if(p.messageType == 2)
            //Message type 2 is (array of neighbors)
            {
                rcvdNeighbors = new NodeID[(p.oneHopNeighbors).length] ;
                int rcvdNeighborLength = rcvdNeighbors.length;
                allHopLength = allHopNeighbors.length;
                for(int i = 0; i < rcvdNeighborLength; i++)
                {
                    rcvdNeighbors[i] = p.oneHopNeighbors[i];
                }
                System.out.println("Message 2 was received from " + (message.source).getID());

                //int rcvdNeighborLength = rcvdNeighbors.length;
                //parse for what's needed
                for(int i = 0; i < rcvdNeighborLength; i++)
                {
                    int k = allHopNeighbors.length - 1;
					while(!(k < 0)) {
						if(rcvdNeighbors[i] != allHopNeighbors[k])
							k = k - 1;

						else
							break;
					}

					if(k < 0 && rcvdNeighbors[i] != myID)
					{
						//Output
						stream.println("Neighbors should be printed HERE!!!!");
					}
                } 
                rcvdMessages++;
            }    
        } 
        if(rcvdMessages == neighborLength * 2)
        {
            notifyAll();
            System.out.println("Receive Notify method entered....");
        } 
    }
    
    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor)
    {
        for(int i = 0; i < oneHopNeighbors.length; i++)
        {
            if(neighbor.getID() == oneHopNeighbors[i].getID())
            {
                brokenNeighbors[i] = true;
                notifyAll();
                System.out.println("Broken Notify method entered....");
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
        for(int i = 0; i < neighborLength; i++)
        {
            Payload p = new Payload(1, myRound, oneHopNeighbors);
            Message msg = new Message(myID, p.toBytes());
            myNode.send(msg, oneHopNeighbors[i]);
            System.out.println("Sending message for round " + myRound);
        }
        
        
        while(rcvdMessages < neighborLength * 2)
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
            return;
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
        oneHopNeighbors = myNode.getNeighbors();
        neighborLength = oneHopNeighbors.length;
        allHopNeighbors = oneHopNeighbors;

		brokenNeighbors = new boolean[oneHopNeighbors.length];
		for(int i = 0; i < oneHopNeighbors.length; i++)
		{
			brokenNeighbors[i] = false;
		}
		terminating = false;
        
        //write neighbors to output file
        System.out.println("Printing out the list of neighbors printed in line one of the output file.");
		//Setup file information
		outputFileName = myID.getID() + "-" + configFile;
		outputFile = new File(outputFileName);
		try {
			stream = new PrintStream(outputFile);
			stream.print("1: ");
			for(int i = 0; i < oneHopNeighbors.length; i++) {
				stream.print(oneHopNeighbors[i].getID() + " ");
			}
			stream.println();
		}

		catch(FileNotFoundException ex) {

		}

        myRound = 1;

        numOfNodes = 5;
        
        //Node initiates neighbor detection
        while (myRound < numOfNodes)
        {
            if(map.get(myRound) != null)
            {
                receive(map.get(myRound));
            }
            System.out.println("Round " + myRound);
            discoverNeighbors();
            myRound++;
            int rcvdMessages = 0;
        }
        
        myNode.tearDown();

        for(int i = 0; i < oneHopNeighbors.length; i++)
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

