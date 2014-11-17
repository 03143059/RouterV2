 /**
 * Broadcaster.java --  This class is used to send a packet from 
 * one router to another. The packet contains the id of the sending 
 * router and its distance vector.
 *
 * It implements "Runnable" so that it can be run as a separate thread.
 */

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

 public class Broadcaster implements Runnable {
    private Neighbor to;    // Sending router
    private Neighbor from;  // Receiving router

    /////////////////////////////////////////////////////
    // Constructor:
    /////////////////////////////////////////////////////
    public Broadcaster(Neighbor from, Neighbor to) {
        this.from = from;
        this.to = to;
    }

    /////////////////////////////////////////////////////
    // Most of the work is done here:
    /////////////////////////////////////////////////////
    public void run() {

        /////////////////////////////////////////////////////
        // For ease of I/O (since we haven't yet discussed
        // binary packet data), convert the distance vector into
        // a String so it can be read using a Scanner:
        /////////////////////////////////////////////////////
        String dvs = convert(from.getDv());

        /////////////////////////////////////////////////////
        // Make ten attempts to send the packet, then give up. The ten attempts
        // are separated by a one-second wait. This is simply to give the user
        // enough time to manually start up all the routers.
        /////////////////////////////////////////////////////
        Socket sock = null;
        boolean done = false;
        int tries = 0;

        while (!done && tries < 10) {
            try {
                Thread.sleep(1000); // long wait due to manual startup

                sock = new Socket(to.getAddr(), to.getPort());
                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                out.writeBytes(dvs);
                sock.close();
                done = true;
            } catch (Exception e) {
                tries++;
                if (tries >= 10)
                    System.out.println(from.getId() + " giving up sending to " +
                            to.getId());
            }
        }
    }

    /////////////////////////////////////////////////////
    // Utility to take an integer vector and convert it
    // into a string of numbers separated by spaces.
    // Precede this with the id of the sender and terminate
    // it with a newline character so it can be scanned by the
    // recipient using "nextLine()".
    /////////////////////////////////////////////////////
    public String convert(HashMap<String, Integer> dv) {
        String result = "" + from.getId();
        for (String name : dv.keySet()) {
            result += " " + name + ":" + dv.get(name);
        }
        return result + "\n";
    }
}
