/**
 * Broadcaster.java --  This class is used to send a packet from 
 * one router to another. The packet contains the id of the sending 
 * router and its distance vector.
 *
 * It implements "Runnable" so that it can be run as a separate thread.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BroadcastingService implements Runnable {
    private Neighbor to;    // Sending router
    private Neighbor from;  // Receiving router

    public static HashMap<Neighbor, Socket> sockets = new HashMap<Neighbor, Socket>();

    public static Socket getSocket(Neighbor from, Neighbor to) throws IOException {
        if (!sockets.containsKey(to)){
            // crear socket
            Setup.println("[Broadcaster.getSocket] Creando socket a " + to.getAddr().getHostAddress());
            Socket socket = new Socket(to.getAddr(), to.getPort());
            sockets.put(to, socket);
            // enviar hello
            try {
                Setup.println("[Broadcaster.getSocket] Enviando hello a " + to.getAddr().getHostAddress());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeBytes(getHelloMsg(from));
                out.flush();
                //  sock.close();
            } catch (Exception e) {
                Setup.println("[Broadcaster.getSocket] No es posible enviar HELLO a" +
                        to.getAddr().getHostAddress());
            }
            return socket;
        }
        Setup.println("[Broadcaster.getSocket] Reutilizando socket a " + to.getAddr().getHostAddress());
        return sockets.get(to);
    }

    private static String getHelloMsg(Neighbor from) {
        StringBuilder result = new StringBuilder();
        result.append("From:");
        result.append(Setup.ROUTER_NAME);
        result.append("\n");
        result.append("Type:HELLO\n");
        return result.toString();
    }

    /////////////////////////////////////////////////////
    // Constructor:
    /////////////////////////////////////////////////////
    public BroadcastingService(Neighbor from, Neighbor to) {
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
                Thread.sleep(1000); // delay for manual start

                sock = BroadcastingService.getSocket(from, to); // obtener socket del pool

                Setup.println("[BroadcastingService.run] Notificando a " + to.getAddr().getHostAddress());
                Setup.println(dvs.replaceAll("^", "[BroadcastingService.run]\n"));

                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                out.writeBytes(dvs);
                out.flush();

                //  sock.close();
                done = true;
            } catch (Exception e) {
                tries = 10;//tries++;
                //   if (tries >= 10)
                Setup.println("[BroadcastingService.run] No es posible enviar a " +
                        to.getAddr().getHostAddress());

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

        // El mensaje debe ser en la forma IP:costo para cada elemento del dv en vez del espacio como separador del vector
        StringBuilder result = new StringBuilder();
        result.append("From:");
        result.append(Setup.ROUTER_NAME);
        result.append("\n");
        result.append("Type:DV\n");
        result.append("Len:");
        result.append(dv.size());
        result.append("\n");
        for(String name : dv.keySet()) {
            int costo = dv.get(name);
            result.append(name);
            result.append(":");
            result.append(costo);
            result.append("\n");
        }
        return result.toString();
    }
}
