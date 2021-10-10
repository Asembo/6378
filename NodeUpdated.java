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
	Message message;
	NodeID[] neighbors;
	File file;
	int numNodes = 0;
	String hostName = "";
	String [] neighborName = new String [2];
	String listeningPort = "";
	String [] neighborPort = new String [2];
	int [] neighborPortInt = new int [2];
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
			Scanner neighborScanner2 = new Scanner(file);
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
							   }
							}	

							for(int j = 0; j < neighborID.length; j++){
								while(neighborScanner.hasNextLine()) {
									neighborLine = neighborScanner.nextLine();
									if(!neighborLine.startsWith("#")){
										int temp = neighborID[j];
										temp++;
										if(m == temp){
											String[] neighborLines = neighborLine.split(" ");
											neighborName[j] = neighborLines[1];
											neighborPort[j] = neighborLines[2];
											}
										m++;
										}
								   }
								   m = 0;

								   while(neighborScanner2.hasNextLine()) {
									neighborLine = neighborScanner2.nextLine();
									if(!neighborLine.startsWith("#")){
										int temp2 = neighborID[1];
										temp2++;
										if(m == temp2){
											String[] neighborLines = neighborLine.split(" ");
											neighborName[1] = neighborLines[1];
											neighborPort[1] = neighborLines[2];
											}
										m++;
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

		int listeningPortInt = Integer.parseInt(listeningPort);
		
		SocketServer s = new SocketServer();
		s.runServer(listeningPortInt);
		System.out.println("Server is running");
		
		System.out.println("NeighborID: " + Arrays.toString(neighborID));
		System.out.println("NeighborName: " + Arrays.toString(neighborName));
		System.out.println("NeighborPort: " + Arrays.toString(neighborPort));

		for(int i = 0; i < neighborPort.length; i++) {
			neighborPortInt[i] = Integer.parseInt(neighborPort[i]);
			System.out.println("NeighborPortInt: " + neighborPortInt[i]);
		}
	}
	
	// methods
	public NodeID[] getNeighbors()
	{
		System.out.println(neighbors[0]);
		System.out.println(neighbors[1]);

		for(int j = 0; j < neighborID.length; j++){
			System.out.println(j + " neighborID: " + neighborID[j]);
		}

		return neighbors;
	}

	public void send(Message message, NodeID destination)
	{
		Socket clientConnection;
		System.out.println("Entered the send method");
		//establish dedicated connection to each neighbor using TCP/IP sockets
		//retain the same socket till program terminates	
		for(int i = 0; i < neighbors.length; i++){
			System.out.println(i);
			if(destination == neighbors[i]){
				while(true) {
					try {
						String neighborNameString = neighborName[i].toString();
						System.out.println("NeighborNameString: " + neighborNameString);
						clientConnection = new Socket(neighborNameString, neighborPortInt[i]); 	//neighborHostName, neighborListeningPort
						OutputStream output = clientConnection.getOutputStream();
						ObjectOutputStream objectOut = new ObjectOutputStream(output);
						System.out.println("Sending message to server socket");
						objectOut.writeObject(message);
					}

					catch(SocketTimeoutException ex) {
						System.out.println("Trying to connect");
					}

					catch(UnknownHostException ex) {

					}

					catch (IOException e) {

					}
				}
			}
		}
	}

	public void sendToAll(Message message)
	{
		Socket clientConnection;
		System.out.println("Entered the sendToAllMethod");
		for(int i = 0; i < neighbors.length; i++){
			System.out.println(i);
			while(true) {
				try {
					String neighborNameString = neighborName[i].toString();
					System.out.println("NeighborNameString: " + neighborNameString);
					clientConnection = new Socket(neighborNameString, neighborPortInt[i]);
					OutputStream output = clientConnection.getOutputStream();
					ObjectOutputStream objectOut = new ObjectOutputStream(output);
					System.out.println("Sending message to server socket");
					objectOut.writeObject(message);					
				}

				catch(SocketTimeoutException ex) {
						System.out.println("Trying to connect");
				}

				catch(UnknownHostException ex) {

				}

				catch (IOException e) {

				}
				
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
