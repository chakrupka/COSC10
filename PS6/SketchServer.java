import com.sun.security.jgss.GSSUtil;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A server to handle sketches: getting requests from the clients,
 * updating the overall state, and passing them on to the clients
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022, added communication to master sketch
 */
public class SketchServer {
	private ServerSocket listen;						// for accepting connections
	private ArrayList<SketchServerCommunicator> comms;	// all the connections with clients
	private Sketch sketch;								// the state of the world

	public SketchServer(ServerSocket listen) {
		this.listen = listen;
		sketch = new Sketch();
		comms = new ArrayList<SketchServerCommunicator>();
	}

	/**
	 * Returns the sketch
	 * @return Master sketch
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Adds a shape to the master sketch
	 * @param shape Shape to be added
	 */
	public synchronized void addShape(Shape shape) {
		sketch.addShape(shape);
		broadcast("SKETCH "+HandleMessage.sketchToStr(sketch.getShapeMap())); // send updated sketch out
	}

	/**
	 * Delete / remove a shape from the master sketch
	 * @param id ID of the shape to be removed
	 */
	public synchronized void delShape(int id) {
		sketch.removeShape(id);
		broadcast("SKETCH "+HandleMessage.sketchToStr(sketch.getShapeMap())); // send updated sketch out
	}

	/**
	 * Update a shape in the sketch
	 * @param id ID of shape to be updated
	 * @param shape Shape to replace the matching ID shape in the master sketch
	 */
	public synchronized void updateShape(int id, Shape shape) {
		sketch.updateShape(id, shape);
		broadcast("SKETCH "+HandleMessage.sketchToStr(sketch.getShapeMap())); // send updated sketch out
	}

	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		System.out.println("server ready for connections");
		while (true) {
			SketchServerCommunicator comm = new SketchServerCommunicator(listen.accept(), this);
			comm.setDaemon(true);
			comm.start();
			addCommunicator(comm);
		}
	}

	/**
	 * Adds the communicator to the list of current communicators
	 */
	public synchronized void addCommunicator(SketchServerCommunicator comm) {
		comms.add(comm);
	}

	/**
	 * Removes the communicator from the list of current communicators
	 */
	public synchronized void removeCommunicator(SketchServerCommunicator comm) {
		comms.remove(comm);
	}

	/**
	 * Sends the message from the one communicator to all (including the originator)
	 */
	public synchronized void broadcast(String msg) {
		for (SketchServerCommunicator comm : comms) {
			comm.send(msg);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new SketchServer(new ServerSocket(4242)).getConnections();
	}
}
