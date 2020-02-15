package server;

import server.Server;

import java.io.IOException;
import java.net.ServerSocket;

// DOne with the help of https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
public class FantasyRealmsTMServer {

    public static void main(String[] args) throws IOException {
        try {
            Thread t = new Server(new ServerSocket(1234));

            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
