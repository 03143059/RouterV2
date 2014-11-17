/**
 * Created by Werner on 9/10/2014.
 * http://ipsit.bu.edu/sc546/sc546Fall2002/RIP2/RIP/
 */

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutingService implements Runnable {
    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    protected InetAddress address;

    //region Static methods

    static RoutingService server = null;

    public static boolean isServerRunning(){
        return server != null && server.isRunning;
    }

    public static void stop() {
        server.stopServer();
        server = null;
    }

    public static void start(InetAddress address, int port) {
        Setup.println("Router iniciado en " + address.getHostAddress() + ":" + port);
        server = new RoutingService(address, port);
        new Thread(server).start();
    }

    //endregion // static methods

    //region instance methods

    public RoutingService(InetAddress address, int port) {
        this.serverPort = port;
        this.address = address;
    }

    protected boolean isRunning = false;

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort, 0, address);
            isRunning = true;
        } catch (IOException e) {
            isRunning = false;
            this.serverSocket = null;
            isStopped = true;
            Setup.println("[RoutingService.openServerSocket] Router detenido.");
            throw new RuntimeException("No se puede abrir el puerto " + serverPort, e);
        }
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
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
                    new RouterWorker(clientSocket,
                            "Thread Pooled Server"));
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

    //endregion // instance methods

    class RouterWorker implements Runnable {

        protected Socket clientSocket = null;
        protected String serverText = null;

        public RouterWorker(Socket clientSocket, String serverText) {
            this.clientSocket = clientSocket;
            this.serverText = serverText;
            Setup.println("[RoutingService] Conexion abierta desde: " +
                    clientSocket.getRemoteSocketAddress());
        }

        public void run() {
            try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                long time = System.currentTimeMillis();
                output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                        this.serverText + " - " +
                        time +
                        "").getBytes());
                output.close();
                input.close();
                Setup.println("[RouterWorker.run]: " + time);
            } catch (IOException e) {
                //report exception somewhere.
                e.printStackTrace();
            }
        }
    }
}
