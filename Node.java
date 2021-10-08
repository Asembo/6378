import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.net.*;
import java.io.ObjectOutputStream;

//Object to reprsents a node in the distributed system
public class Node
{
	// node identifier
	private NodeID identifier;
	NodeID[] neighbors;
	File file;
	int numNodes = 0;
	String hostName = "";
	String listeningPort = "";
	String neighborPort;
	String neighborName;
	NodeID newID;
	Socket clientConnection = new Socket();
	
	// constructor
	public Node(NodeID identifier, String configFile, Listener listener)
	{
		this.identifier = identifier;
		file = new File(configFile);
		neighbors = new NodeID[10];
		
		try {
			Scanner scanner = new Scanner(file);
			Scanner neighborScanner = new Scanner(file);
			int n = 0;
			int m = 0;
			String line;
			String neighborLine;
			
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				if(!line.startsWith("#"))
				{
					if(n == 0)
					{
						numNodes = Integer.parseInt(line);
					}
					if(n == identifier.getID()+1)
					{
						String[] lines = line.split(" ");
						hostName = lines[1];
						listeningPort = lines[2];
					}
					if(n == numNodes+identifier.getID()+1)
					{
						String[] lines = line.split(" ");
						   for(int i = 0; i < lines.length; i++)
						   {
							   if(lines[i].contains("#")) {
								   i = lines.length;
							   }
							   else{
								   NodeID newID = new NodeID(Integer.parseInt(lines[i]));
								   neighbors[i] = newID;
							   }
						   }
					}
					n++;
				}
			}
			while(neighborScanner.hasNextLine()) {
				if(!neighborLine.startsWith("#")){
					for(int i = 0; i < lines.length; i++){
						do{
							neighborLine = neighborScanner.nextLine();
							m++;
						}while(m != neighborID[i]+ 2);
						String[] neighborLines = neighborLine.split(" ");
							neighborName = lines[1];
							neighborPort = lines[2];

						//if(m == neighborID[i]+ 2){
						//	String[] neighborLines = neighborLine.split(" ");
						//	neighborName = lines[1];
						//	neighborPort = lines[2];
						//}
						//m++;
					}
					
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
		}
	
	}


	int listeningPortInt = Integer.parseInt(listeningPort);
	int neighborPortInt = Integer.parseInt(neighborPort);
	
	public void main(String[] args){
		SocketServer s = new SocketServer();
		s.runServer(listeningPortInt);
	}

	// methods
	public NodeID[] getNeighbors()
	{
		return neighbors;
	}

	public void send(Message message, NodeID destination)
	{
		String hostName;
		String listeningPort;
		Socket clientConnection;
		
		//establish dedicated connection to each neighbor using TCP/IP sockets
		//retain the same socket till program terminates	
		for(int i = 0; i < neighbors.length; i++){
			if(destination == neighbors[i]){
				try{
					clientConnection = new Socket(neighborName, neighborPortInt); 
					OutputStream output = clientConnection.getOutputStream(); 
					ObjectOutputStream objectOut = new ObjectOutputStream(output);  
					objectOut.writeObject(message);
				}
				catch(UnknownHostException ex){
				//mostly nothing to do here
				}
				catch(IOException e){
				//nothing 
				}
				break;
			}
		}
	}

	public void sendToAll(Message message)
	{
		for(int i = 0; i < neighbors.length; i++){
			try{
				//clientConnection = new Socket(neighborName, neighborPortInt); 
				OutputStream output = clientConnection.getOutputStream(); 
				ObjectOutputStream objectOut = new ObjectOutputStream(output);  
				objectOut.writeObject(message);
			}
			catch(UnknownHostException ex){
			//mostly nothing to do here
			}
			catch(IOException e){
			//nothing 
			}
		}
	}	
	
	public void tearDown ()
	{
		try{
			clientConnection.close();
		}
		catch(IOException e){
			//
		}
	}
}
