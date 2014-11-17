/**
 * Created by Werner on 9/10/2014.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutingService implements Runnable {
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private String id;
    private InetAddress addr;
    private int port;
    private ArrayList<NbrCostPair> nbrList;
    private HashMap<String, Integer> dv;
    private Neighbor myself;
    public static final int INFINITY = 9999;


    //region Static methods

    static RoutingService server = null;

    public static boolean isServerRunning() {
        return server != null && server.isRunning;
    }

    public static void stop() {
        server.stopServer();
        server = null;
    }

    public static void start() {
        Setup.println("Router iniciado en " + Setup.address.getHostAddress() + ":" + Setup.ROUTING_PORT);
        server = new RoutingService();
        new Thread(server).start();
    }

    //endregion // static methods

    //region instance methods


    protected boolean isRunning = false;

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(Setup.ROUTING_PORT, 0, Setup.address);
            isRunning = true;
        } catch (IOException e) {
            isRunning = false;
            this.serverSocket = null;
            isStopped = true;
            Setup.println("[RoutingService.openServerSocket] Router detenido.");
            throw new RuntimeException("No se puede abrir el puerto " + Setup.ROUTING_PORT, e);
        }
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        this.id = Setup.ROUTER_NAME;
        this.addr = Setup.address;
        this.port = Setup.ROUTING_PORT;
        this.nbrList = Setup.nbrList;
        dv = new HashMap<String, Integer>();

        Setup.println("Starting Router <" + id + "> on port " + port);
        Setup.println("Neighbors: " + getNbrString());
        Setup.println();

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

        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    Setup.println("[RoutingService.run] Router detenido.");
                    return;
                }
                throw new RuntimeException(
                        "Error aceptando conexion del cliente", e);
            }
            this.threadPool.execute(
                    new RouterWorker(clientSocket));
        }
        this.threadPool.shutdown();
        Setup.println("[RoutingService.run] Router detenido.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stopServer() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error deteniendo el servidor", e);
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
    public void printDv(String fromId, HashMap<String, Integer> dv) {
        Setup.println("<<From neighbor " + fromId + ">>");
        for (String n : dv.keySet()) {
            Setup.print(n + ":" + dv.get(n) + " ");
        }
        Setup.println();
        Setup.println();
    }

    public void printTable() {
        Setup.println("                     DISTANCE TABLE");
        Setup.println("----------------------------------------------------------");
        Setup.print("\t\t");
        for (String n : dv.keySet()) {
            Setup.print("\t" + n);
        }
        Setup.println();

        // print mine
        Setup.print(id);
        for (String n : dv.keySet()) {
            Setup.print("\t\t" + dv.get(n));
        }
        Setup.println();

        // print neighbors'
        for (NbrCostPair ncp : nbrList) {
            HashMap<String, Integer> ndv = ncp.getNbr().getDv();
            Setup.print(ncp.getNbr().getId());
            for (String n : dv.keySet()) {
                Integer distance = ndv.get(n);
                Setup.print("\t\t" + ((distance == null) ? "NaN" : distance));
            }
            Setup.println();
        }
        Setup.println();
    }

    public void distribute() {
        for (NbrCostPair ncp : nbrList) {
            BroadcastingService br = new BroadcastingService(myself, ncp.getNbr());
            Thread t = new Thread(br);
            t.start();
        }
    }

    //endregion // instance methods

    class RouterWorker implements Runnable {

        protected Socket clientSocket = null;


        public RouterWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            Setup.println("[RoutingService.run] Conexion abierta desde: " + clientSocket.getInetAddress().getHostAddress());
        }

        public void run() {

            BufferedReader in = null;

            // Now loop forever, receiving updates from neighbors::
            try {
                clientSocket.setSoTimeout(0); // infinite timeout = keep alive
                //// Open a socket, read the distance vector (in my program I
                //// called the sender "fromId" and its distance vector "fromdv")
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //get From:<Name Router>
                String line = in.readLine();
                //tokenizer From
                StringTokenizer st = new StringTokenizer(line, ":");
                //ignore "From"
                st.nextToken();
                //get name of Router
                String fromId = st.nextToken();

                //get "Type:<type>"
                line = in.readLine();
                //tokenizer Type
                st = new StringTokenizer(line, ":");
                //ignore "Type"
                st.nextToken();
                //get type of message
                String msgType = st.nextToken();

                if (msgType.equals("HELLO")) {
                    Setup.println("[RouterWorker.run] HELLO from " + fromId);
                    String message = "From:" + addr.getHostAddress() + "\nType:WELCOME\n";
                    DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                    outToClient.writeBytes(message + '\n');
                    outToClient.flush();
                } else {
                    throw new Exception("Tipo de mensaje invalido");
                }

                while (true) {
                    // recibir más rutas
                    InetAddress fromAddr = this.clientSocket.getInetAddress();
                    HashMap<String, Integer> fromdv = new HashMap<String, Integer>();

                    //get From:<Name Router>
                    line = in.readLine();
                    //tokenizer From
                    st = new StringTokenizer(line, ":");
                    //ignore "From"
                    st.nextToken();
                    //get name of Router
                    fromId = st.nextToken();

                    //get "Type:<type>"
                    line = in.readLine();
                    //tokenizer Type
                    st = new StringTokenizer(line, ":");
                    //ignore "Type"
                    st.nextToken();
                    //get type of message
                    msgType = st.nextToken();

                    if (!msgType.equals("DV"))
                        throw new Exception("Tipo de mensaje invalido");

                    //get "Len:<leb>"
                    line = in.readLine();
                    //tokenizer Type
                    st = new StringTokenizer(line, ":");
                    //ignore "Len"
                    st.nextToken();
                    //get length
                    int len = Integer.parseInt(st.nextToken());
                    //for to save distanceVectorTable
                    for (int i = 1; i <= len; i++) {
                        //get first line of request from client
                        String input = in.readLine();
                        if (input == null) throw new Exception("Solicitud invalida");
                        StringTokenizer parse = new StringTokenizer(input, ":");
                        String fname = parse.nextToken();
                        int fdv = Integer.parseInt(parse.nextToken());
                        //update table
                        fromdv.put(fname, fdv);
                    }

                    // FOR DEBUGGING:
                    printDv(fromId, fromdv);

                    boolean change = false;
                    for (String n : fromdv.keySet()) {
                        ////// Update my own distance vector and routing table:

                        if (!dv.containsKey(n)) {
                            dv.put(n, INFINITY);
                        }

                        int bc = getNbrCost(fromId);

                        if (dv.get(n) > bc + fromdv.get(n)) {
                            change = true;
                            Setup.print(String.format("<<Better route to %s>>\ncurr: %d, new: %d [%d + %d]\n\n", n, dv.get(n), bc + fromdv.get(n), bc, fromdv.get(n)));
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
                        Setup.println("<<Change detected>>");
                        Setup.println("Broadcasting...");
                        Setup.println();
                        ///// DISTRIBUTE THE UPDATED VECTOR TO ALL NEIGHBORS
                        distribute();

                    }

                    printTable(); // print routing table

                    // don't close socket
                    // clientSocket.close();

                } // end-while true
            } catch (SocketTimeoutException ste) {
                Setup.println("[RouterWorker.run] Tiempo de espera de conexion agotado");
            } catch (Exception ioe) {
                Setup.println("[RouterWorker.run] Error de servidor: " + ioe);
                ioe.printStackTrace();
            } finally {
                try {
                    in.close(); //close character input stream
                    clientSocket.close(); //close socket connection
                } catch (Exception e) {
                    Setup.println("[RouterWorker.run] Error cerrando conexion: " + e);
                }
                Setup.println("[RouterWorker.run] Conexion cerrada.");
            }
        }


    }
}
