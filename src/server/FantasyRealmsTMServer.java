package server;

import java.io.IOException;
import java.net.ServerSocket;

// DOne with the help of https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/

/**
 * Main class for the application.
 * @author Tereza Miklóšová
 */
public class FantasyRealmsTMServer {

    /**
     * Main method to start the application.
     * @param args Arguments for the application.
     */
    public static void main(String[] args){
        try {
            if(args.length < 1) {
                while(true) {
                        Thread t = new Server(new ServerSocket(3456), args);

                        t.start();
                        while (((Server) t).isRunning()) {

                        }
                        t.join();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
