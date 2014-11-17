/**
 * Neighbor.java -- encapsulates the information about a router.
 * Strictly speaking, this class should not have been necessary, since it
 * duplicates a lot of the information in class "Router".
 *
 * However, due to time constraints this is how the program evolved; a
 * nice project would be to refactor the code for this simulation.
 */

import java.net.InetAddress;
import java.util.HashMap;

public class Neighbor {
    private String id; // this neighbor's id
    private InetAddress addr; // this neighbor's IP address
    private int port; // this neighbor's port
    private HashMap<String, Integer> dv; // this neighbor's row in the network cost table
    public int UpdateCount = 0;

    /////////////////////////////////////////////////////////////
    // Constructor:
    /////////////////////////////////////////////////////////////
    public Neighbor(String id, InetAddress addr, int port, HashMap<String, Integer> dv) {
        this.id = id;
        this.addr = addr;
        this.port = port;
        this.dv = dv;
    }

    public String getId() {
        return id;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public int getPort() {
        return port;
    }

    public HashMap<String, Integer> getDv() {
        return dv;
    }

}
