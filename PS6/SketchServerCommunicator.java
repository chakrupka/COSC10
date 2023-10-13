import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022, finished communcation between server and client
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			out.println("SKETCH "+HandleMessage.sketchToStr(server.getSketch().getShapeMap()));

			// Keep getting and handling messages from the client
			String line;
			while ((line = in.readLine()) != null) {
				String[] newLine = line.split(">");
				switch (newLine[0]) { // check the type of command, and respond adequately
					case "GET SKETCH" -> {
						out.println("SKETCH " + HandleMessage.sketchToStr(server.getSketch().getShapeMap()));
					}
					case "ADD SHAPE" -> {
						server.addShape(HandleMessage.strToShape(newLine[1]));
					}
					case "UPDATE" -> {
						String[] update = line.split("UPDATE>")[1].split(";");
						server.updateShape((Integer.parseInt(update[0])), HandleMessage.strToShape(update[1]));
					}
					case "REMOVE" -> {
						server.delShape(Integer.parseInt(newLine[1]));
					}
				}
			}
			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}