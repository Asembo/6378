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
	String [] neighborName = new String [2];
	String listeningPort = "";
	String [] neighborPort = new String [2];
	NodeID newID;
	int [] neighborID = new int [2];
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
						System.out.println("hostName: " + hostName);
						listeningPort = lines[2];
						System.out.println("listeningPort: " + listeningPort);
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
								   neighborID[i] = (Integer.parseInt(lines[i]));
								   int p = neighborID[i];
								   /*while(neighborScanner.hasNextLine()){
									   neighborLine = neighborScanner.nextLine();
									   if(!neighborLine.startsWith("#")){
										   String [] neighborLines = neighborLine.split(" ");
										if(n == Integer.parseInt(neighborLines[0])){
											neighborName[i] = neighborLines[1];
											neighborPort[i] = neighborLines[2];
										}
									   }
									   
								   }*/
							   }
							   for(int j = 0; j < neighborID.length; j++){
								while(neighborScanner.hasNextLine()) {
									neighborLine = neighborScanner.nextLine();
										if(!neighborLine.startsWith("#")){
											if(m == neighborID[i]+ 1){
											String[] neighborLines = neighborLine.split(" ");
											neighborName[j] = neighborLines[1];
											neighborPort[j] = neighborLines[2];
										}
										m++;
										}
									   }
							   }
							}	
							
					}
					n++;
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
		}
		System.out.println("NeighborID: " + Arrays.toString(neighborID));
		System.out.println("NeighborName: " + Arrays.toString(neighborName));
		System.out.println("NeighborPort: " + Arrays.toString(neighborPort));
	}
	//int listeningPortInt = Integer.parseInt(listeningPort);
	//int neighborPortInt = Integer.parseInt(neighborPort);
	
	public void main(String[] args){
		SocketServer s = new SocketServer();
		s.runServer(1);
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
					clientConnection = new Socket(neighborName[destination.getID()], 11233); 
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
		for(int i = 0; i < neighborID.length; i++){
			try{
				clientConnection = new Socket(neighborName[i], 11233); 
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
