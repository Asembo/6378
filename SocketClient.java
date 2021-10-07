import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.net.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferReader;

public class Client {

    //parameters
    String hostName;
    int listeningPort;
    Socket clientConnection;
    PrintWriter output;
    Message message;

    //constructor
	public void socketClient()
	{
		//Your code goes here
		//establish dedicated connection to each neighbor using TCP/IP sockets

        NodeID neighbors = getNeighbors(); //run function that returns array of current Node's neighbors
		for(i = 0; i < neighbors.length; i++){ //for each neighbor establish connection
            
			//retain the same socket till program terminates	
			while (true) {
				try{
					Socket clientConnection = new Socket("hostname", listeningPort); //for each neighbor, establish a connection 

                    Printwriter output = new PrintWriter(clientConnection.getOutputStream(), true); 
                    byte [] data = output.println (message);  
                    Message message =  (NodeID myID, byte [] data);
				}
				catch(UnknownHostException ex){
					//mostly nothing to do here
				}
                catch(IOException e){
                    //nothing 
                }
				break;
			}
            send(Message message, NodeID neighbors[i]);
		}
        teardown();
	}

}
