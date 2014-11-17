import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class Router implements Runnable {
    private int n;
    private int id;
    private InetAddress addr;
    private int port;
    private ArrayList<NbrCostPair> nbrList;
    private int[] dv;
    private int[] next;
    private Neighbor myself;
    public static final int INFINITY = 999;

    ////////////////////////////////////////////
    // Constructor:
    ////////////////////////////////////////////
    public Router(int n, int id, InetAddress addr, int port,
                  ArrayList<NbrCostPair> nbrList) {
        this.n = n;
        this.id = id;
        this.addr = addr;
        this.port = port;
        this.nbrList = nbrList;
        dv = new int[n];
        next = new int[n];
    }

    /////////////////////////////////////////////
    // In lieu of a "main" method, I called this "run".
    // However, this class does not implement "Runnable",
    // so method "run" is invoked just like any other method.
    /////////////////////////////////////////////
    public void run() {

        System.out.println("Starting Router <" + id + "> on port " + port);
        System.out.println("Network size: " + n);
        System.out.println("Neighbors: " + getNbrString());
        System.out.println();

        /////////////////////////////////////////////
        // Initialize the router's "distance" and "next router" vectors:
        /////////////////////////////////////////////

        // First, initialize distance vector to all "infinity" and
        // next router vector to "none" (I use an invalid number -1 for this):
        for (int i = 0; i < n; i++) {
            dv[i] = INFINITY;
            next[i] = -1;
        }

        // Now modify distance vector with information about immediate neighbors.
        // The "next router" entry is just the neighbor's id.
        for (NbrCostPair ncp : nbrList) {
            dv[ncp.getNbr().getId()] = ncp.getCost();
            next[ncp.getNbr().getId()] = ncp.getNbr().getId();
        }

        // Finally, distance to myself is always 0:
        dv[id] = 0;
        next[id] = id;

        /////////////////////////////////////////////
        // For consistency I must create a "Neighbor" object
        // for myself. (See header comments about refactoring code.)
        /////////////////////////////////////////////
        myself = new Neighbor(n, id, addr, port, dv);

        printTable(); // print table first

        // Send distance vector to all neighbors:
        distribute();

        // Now loop forever, receiving updates from neighbors::

        ServerSocket listen = null;
        try {
            listen = new ServerSocket(port);

            while (true) {
                //// Open a socket, read the distance vector (in my program I
                //// called the sender "fromId" and its distance vector "fromdv")
                Socket socket = listen.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();
                StringTokenizer st = new StringTokenizer(line, " ");
                int fromId = Integer.parseInt(st.nextToken());
                int[] fromdv = new int[n];
                for (int i = 0; i < n; i++) {
                    int fdv = Integer.parseInt(st.nextToken());
                    fromdv[i] = fdv;
                }
                socket.close();

                // FOR DEBUGGING:
                printDv(fromId, fromdv);

                boolean change = false;
                for (int i = 0; i < n; i++) {
                    ////// Update my own distance vector and routing table:
                    int bc = getNbrCost(fromId);

                    if (dv[i] > bc + fromdv[i]) {
                        change = true;
                        System.out.printf("Better route to %d: curr: %d, new: %d [%d + %d], next: %d\n", i, dv[i], bc + fromdv[i], bc, fromdv[i], fromId);
                        // Update own Distance Vector
                        dv[i] = bc + fromdv[i];
                        next[i] = fromId;
                    }

                    // Update Routing table
                    for (NbrCostPair ncp : nbrList) {
                        if (ncp.getNbr().getId() == fromId) {
                            ncp.getNbr().getDv()[i] = fromdv[i];
                        }
                    }
                }

                if (change) {
                    System.out.println("Change detected. Broadcasting...");
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

    private int getNbrCost(int fromId) {
        for (NbrCostPair ncp : nbrList) {
            if (ncp.getNbr().getId() == fromId) return ncp.getCost();
        }
        return 0;
    }

    // CONVENIENT UTILITY PROGRAM TO PRINT A DISTANCE VECTOR:
    public void printDv(int fromId, int dv[]) {
        System.out.println("From neighbor " + fromId + ":");
        for (int i = 0; i < n; i++) {
            System.out.print(dv[i] + " ");
        }
        System.out.println();
    }

    public void printTable() {
        System.out.println("       DISTANCE TABLE");
        System.out.println("-----------------------------");
        for (int i = 0; i < n; i++) {
            System.out.print("\t" + i);
        }
        System.out.println();

        // print mine
        System.out.print(id);
        for (int j = 0; j < n; j++) {
            System.out.print("\t" + dv[j]);
        }
        System.out.println();

        // print neighbors'
        for (NbrCostPair ncp : nbrList) {
            int[] ndv = ncp.getNbr().getDv();
            System.out.print(ncp.getNbr().getId());
            for (int j = 0; j < n; j++) {
                System.out.print("\t" + ndv[j]);
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
