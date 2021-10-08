import java.io.*;
import java.util.*;
import java.net.*;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class SocketServer {

	//parameters4
	private NodeID identifier;
	NodeID[] neighbors;
	File file;
	int numNodes = 0;
	String hostName = "";
	int listeningPortInt;

	// constructor
	public void runServer(int listeningPort)
	{
		try{
			ServerSocket incoming = new ServerSocket(listeningPortInt);
			incoming.setReuseAddress(true);
			System.out.println("Waiting for client.....");

			while(true){
			
				Socket clientConnection = incoming.accept();
	
				Controller threadController = new Controller(clientConnection);
				new Thread(threadController).start();
			}
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}	
		finally {
			if(incoming != null) {
				try {
					incoming.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}

private static class Controller implements Runnable{
	private final Socket clientConnection;
    //constructor
    public Controller(Socket clientConnection){
        this.clientConnection = clientConnection;
    }

    public void run(){
        try{
            //Deserialization

            //get the input stream from the client connection
            InputStream input = clientConnection.getInputStream(); 

            //create a data input stream object to read data from
            ObjectInputStream objectIn = new ObjectInputStream(input);
            
            Message msg =  (Message) objectIn.readObject();
            receive(msg);
        }
        catch (IOException e){
            e.printStackTrace;
        }
        ServerSocket incoming.close();
        clientConnection.close();
    }

}
