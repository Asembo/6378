import java.io.*;
import java.util.*;
import java.net.*;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class SocketServer{
	//parameters
	private NodeID identifier;
	NodeID[] neighbors;
	File file;
	int numNodes = 0;
	String hostName = "";
	int listeningPort;
	ServerSocket incoming = null;
		// constructor
		public void runServer(int listeningPort)
		{
			
			/*try{
				incoming = new ServerSocket(listeningPort);
				incoming.setReuseAddress(true);
				System.out.println("Waiting for client.....");

				//while(true){
				
					Socket clientConnection = incoming.accept();
		
					Controller threadController = new Controller(clientConnection);
					new Thread(threadController).start();
				//}
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
			}*/
			
			Runnable serverTask = new Runnable(){
				@Override
				public void run() {
					try{
						incoming = new ServerSocket(listeningPort);
						incoming.setReuseAddress(true);
						System.out.println("Waiting for client.....");

						while(true){
							Socket clientConnection = incoming.accept();
							System.out.println("Client accepted.....");
							Controller threadController = new Controller(clientConnection);
							new Thread(threadController).start();
						}
					}
					catch (IOException e) {
						System.err.println("Unable to connect.....");
						e.printStackTrace();
					}
				}
			};
			Thread serverThread = new Thread(serverTask);
       		serverThread.start();
		}

	private static class Controller implements Runnable{
		private final Socket clientConnection;
		//constructor
		public Controller(Socket socket){
			this.clientConnection = socket;
		}

		public void run(){
			try{
				//Deserialization

				//get the input stream from the client connection
				InputStream input = clientConnection.getInputStream(); 

				//create a data input stream object to read data from
				ObjectInputStream objectIn = new ObjectInputStream(input);
				
				//Message msg =  (Message) objectIn.readObject();
				//receive(msg);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			//clientConnection.close();
		}
	}
}
