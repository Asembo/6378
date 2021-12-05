//Application layer

int intReqDelay;
int csExTime;
int numOfReqs;

public class Application
{
    //Constructor
    public Application(NodeID identifier, String configFile)
    {
        myID = identifier;
        this.configFile = configFile;
    }

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run()
    {
        //Construct DLock
        myDLock = new DLock(myID, configFile);

        //generate requests every intReqDelay amount of time
        for(int i = 0; i < numOfReqs; i++)
        {
            myDLock.lock();
            //enter critical section
            //sleep for specified amount of time  
            Thread.sleep(csExTime);
            
            //Node leaves critical section
            myDLock.unlock();

            Thread.sleep(intReqDelay);
        }
    }
}
