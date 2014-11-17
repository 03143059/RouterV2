import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Router implements Runnable {
    private String id;
    private InetAddress addr;
    private int port;
    private ArrayList<NbrCostPair> nbrList;
    private HashMap<String, Integer> dv;
    private Neighbor myself;
    public static final int INFINITY = 999;

    ////////////////////////////////////////////
    // Constructor:
    ////////////////////////////////////////////
    public Router(String id, InetAddress addr, int port,
                  ArrayList<NbrCostPair> nbrList) {
        this.id = id;
        this.addr = addr;
        this.port = port;
        this.nbrList = nbrList;
        dv = new HashMap<String, Integer>();
    }

    /////////////////////////////////////////////
    // In lieu of a "main" method, I called this "run".
    // However, this class does not implement "Runnable",
    // so method "run" is invoked just like any other method.
    /////////////////////////////////////////////
    public void run() {

        System.out.println("Starting Router <" + id + "> on port " + port);
        System.out.println("Neighbors: " + getNbrString());
        System.out.println();

        /////////////////////////////////////////////
        // Initialize the router's "distance" vector:
        /////////////////////////////////////////////

        // First, initialize distance vector with information about immediate neighbors.
        for (NbrCostPair nbr : nbrList) {
            dv.put(nbr.getNbr().getId(), nbr.getCost());
        }

        // Finally, distance to myself is always 0:
        dv.put(id, 0);

        /////////////////////////////////////////////
        // For consistency I must create a "Neighbor" object
        // for myself. (See header comments about refactoring code.)
        /////////////////////////////////////////////
        myself = new Neighbor(id, addr, port, dv);

        printTable(); // print table first

        // Send distance vector to all neighbors:
        distribute();

        // Now loop forever, receiving updates from neighbors::

        ServerSocket listen = null;
        try {
            listen = new ServerSocket(port, 0, addr);

            while (true) {
                //// Open a socket, read the distance vector (in my program I
                //// called the sender "fromId" and its distance vector "fromdv")
                Socket socket = listen.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();
                StringTokenizer st = new StringTokenizer(line, " ");
                String fromId = st.nextToken();
                HashMap<String, Integer> fromdv = new HashMap<String, Integer>();
                while (st.hasMoreTokens()) {
                    StringTokenizer stp = new StringTokenizer(st.nextToken(), ":");
                    String name = stp.nextToken();
                    int fdv = Integer.parseInt(stp.nextToken());
                    fromdv.put(name, fdv);
                }
                socket.close();

                // FOR DEBUGGING:
                printDv(fromId, fromdv);

                boolean change = false;
                for (String n : fromdv.keySet()) {
                    ////// Update my own distance vector and routing table:

                    if (!dv.containsKey(n)){
                        dv.put(n, INFINITY);
                    }

                    int bc = getNbrCost(fromId);

                    if (dv.get(n) > bc + fromdv.get(n)) {
                        change = true;
                        System.out.printf("<<Better route to %s>>\ncurr: %d, new: %d [%d + %d]\n\n", n, dv.get(n), bc + fromdv.get(n), bc, fromdv.get(n));
                        // Update own Distance Vector
                        dv.put(n, bc + fromdv.get(n));
                    }

                    // Update Routing table
                    for (NbrCostPair ncp : nbrList) {
                        if (ncp.getNbr().getId().equalsIgnoreCase(fromId)) {
                            ncp.getNbr().getDv().put(n, fromdv.get(n));
                        }
                    }
                }

                if (change) {
                    System.out.println("<<Change detected>>");
                    System.out.println("Broadcasting...");
                    System.out.println();
                    ///// DISTRIBUTE THE UPDATED VECTOR TO ALL NEIGHBORS
                    distribute();

                }

                printTable(); // print routing table

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNbrString() {
        String r = "";
        for (NbrCostPair ncp : nbrList) {
            if (r.length() > 0) r += ", ";
            r += ncp.getNbr().getId() + "[" + ncp.getCost() + "]";
        }
        return r;
    }

    private int getNbrCost(String fromId) {
        for (NbrCostPair ncp : nbrList) {
            if (ncp.getNbr().getId().equalsIgnoreCase(fromId)) return ncp.getCost();
        }
        return 0;
    }

    // CONVENIENT UTILITY PROGRAM TO PRINT A DISTANCE VECTOR:
    public void printDv(String fromId,  HashMap<String, Integer> dv) {
        System.out.println("<<From neighbor " + fromId + ">>");
        for (String n : dv.keySet()) {
            System.out.print(n + ":" + dv.get(n) + " ");
        }
        System.out.println();
        System.out.println();
    }

    public void printTable() {
        System.out.println("                     DISTANCE TABLE");
        System.out.println("----------------------------------------------------------");
        System.out.print("\t\t");
        for (String n : dv.keySet()) {
            System.out.print("\t" + n);
        }
        System.out.println();

        // print mine
        System.out.print(id);
        for (String n : dv.keySet()) {
            System.out.print("\t\t" + dv.get(n));
        }
        System.out.println();

        // print neighbors'
        for (NbrCostPair ncp : nbrList) {
            HashMap<String, Integer> ndv = ncp.getNbr().getDv();
            System.out.print(ncp.getNbr().getId());
            for (String n : dv.keySet()) {
                Integer distance = ndv.get(n);
                System.out.print("\t\t" + ((distance==null)?"NaN":distance));
            }
            System.out.println();
        }
        System.out.println();
    }

    public void distribute() {
        for (NbrCostPair ncp : nbrList) {
            Broadcaster br = new Broadcaster(myself, ncp.getNbr());
            Thread t = new Thread(br);
            t.start();
        }
    }
}
