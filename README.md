# CS 6378 Project

AUTHORS:
	Kevin Mashayekhi
	Asembo Timon


INTRODUCTION:
	This program uses socket programming to create a distributed system of nodes,
	where each node is able to send and receive messages from only its neighbors
	defined in the config file. This implementation uses multithreading to enable 
	the server to accept requests from multiple clients at the same time.


OPERATION INSTRUCTIONS:
	1) Connect to CometNet or the UTD VPN to get access to UTD machines
	2) Ensure that all of the files included are placed in the same
	Java Project file
	3) Open project on any dc machine listed in the config file 
	4) Open terminal
	5) In terminal type
		'javac Node.java' to read in the node file on the Java VM
		'javac Main.java' to read in the main file on the Java VM
		'java Main x config.txt' to run the main with a given nodeID,
		indicated by x. 
		NOTE: when running the program replace x with the intended nodeID
	6) Repeat 2-4 on neighboring dc machines to create the neighboring nodes
	server and client connections.


FILE MANIFEST:
	Application.java
	Detects whether a given topology is a ring topology. Contains the receive, broken,
	detectRing, and run methods.

	Listener.java
	Implements the observer design pattern and calls upon the receive and broken 
	methods.

	Main.java
	Main executable program. Reads in the NodeID and the config file information
	from the command line. Launchs and runs application.

	Message.java
	Creates object Message which contains the ID of the node sending the message
	and the message information, converted into bytes. 

	Node.java
	Contains Node constructor that reads in config file into a scanner. Scanner 
	assigns information from the config file into variables numNodes, hostName, 
	listeningPort, and neighbors. These variables are then prepared to be used
	in establish a client-server connection. The variables neighborName and
	neighborPort are also established and used to store information containing
	neighbor hostname and listeningport number. The node constructor also calls
	the SocketServer class to create a server using listeningPort variable.
	The attempted connection to clients is also established in the node constructor
	using the neighborName and neighborPort.
	The method getNeighbors returns the NodeID of the established node neighbors.
	The method send takes in the Message object information and the destiation node
	the message will be sent. The message object is written to the server socket.
	The method sendToAll takes in the Message object information and sends the message
	to all neighboring nodes.
	The method tearDown breaks the connections between all neighboring nodes.

	NodeID.java
	Serializes the nodeID information.
	
	Payload.java
	Serializes the message information 

	SocketServer.java
	Creates the socket used for the client connection. Uses multithreading to allow
	multiple threads to accept multiple requests from multiple clients at the same
	time. Reads in the message sent from the client.

	config.txt
	A text file containing the topology configuration, number of nodes, information
	concerning the nodeID, hostname and listening port for all nodes, and list of 
	neighboring nodes for any given node.
	
