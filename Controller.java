import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.net.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferReader;

public class Controller implements Runnable{

    //constructor
    public Controller(Socket clientConnection){
        this.clientConnection = clientConnection;
    }

    public void run(){
        try{
            BufferReader input = new BufferReader(new InputStreamReader(clientConnection.getInputStream()));
            byte[] data = input.readLine();
            Message message =  (NodeID clientConnection.getID(), byte [] data);
        }
        catch (IOException e){
            e.printStackTrace;
        }
    }

}
