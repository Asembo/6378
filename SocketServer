import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.net.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferReader;

public class SocketServer {

	//parameters4
	private NodeID identifier;
	NodeID[] neighbors;
	File file;
	int numNodes = 0;
	String hostName = "";
	String listeningPort = "";

	// constructor
	public SocketServer()
	{
	try{
		ServerSocket incoming = new ServerSocket(listeningPort);
		System.out.println("Waiting for client.....");
	}
	catch (IOException e){
			System.out.println(e.getMessage());
		}

	//once socket is accepted, retain for lifetime of program
	while(true){
		try{
		Socket clientConnection = incoming.accept();
		System.out.println("Client Accepted");
        
		Controller threadController = new Controller(clientConnection);
		new Thread(threadController).start();
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	
	}
    }
}
