//Object to reprsents a node in the distributed system
class Node
{
	// node identifier
	private NodeID identifier;
	
	// constructor
	public Node(NodeID identifier, String configFile, Listener listener)
	{
		//Your code goes here
		//establish dedicated connection to each neighbor using TCP/IP sockets
		//for each neighbor establish connection
		for(i = 0; i < neighbors.length; i++){
			//retain the same socket till program terminates	
			while (true) {
				try{
					socket = new Socket("hostname", listeningPort); //for each neighbor, establish a connection 
					System.out.println("Connected"); 
				}
				catch(Exception ex){
					//mostly nothing to do here
				}
				break;
			}
		}
		

	}

	// methods
	public NodeID[] getNeighbors()
	{
		//Your code goes here
	}

	public void send(Message message, NodeID destination)
	{
		//Your code goes here
	}

	public void sendToAll(Message message)
	{
		//Your code goes here
	}
	
	public void tearDown()
	{
		//Your code goes here
	}
}
