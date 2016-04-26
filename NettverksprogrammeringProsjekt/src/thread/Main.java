package thread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * WebSocket Server communicating with multiple clients
 * 
 * @author Aksel
 */
public class Main {
    
    public static ArrayList<ServerThread> list;  // threads handling active clients
    public static ArrayList<ServerThread> trash; // threads that were handling disconected clients
    
    /**
     * Initializes a ServerSocket on port 80
     * Creates one thread for each client 
     * 
     * @param args String-array
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        try(ServerSocket ss = new ServerSocket(80)) {
            list = new ArrayList<ServerThread>();
            trash = new ArrayList<ServerThread>();
            boolean run = true;
            try {               
            
                while (run) {
                    Socket connection = ss.accept();
                    ServerThread thread = new ServerThread(connection);
                    thread.start();
                    list.add(thread);
                }
                
            } catch (Exception e) {
                e.printStackTrace();  
            }
            finally {
                for (ServerThread t : list) {
                    t.OnClose(); // sends closing-handshake
                    t.close(); // close IO-streams
                    t.join();
                }
                for (ServerThread t : trash) {
                    t.join();
                }
            }
        }
    }
    
    /**
     * Sends the given message to each of the active clients
     * 
     * @param message the message which will get sent to all clients
     */
    public synchronized void OnMessage(byte[] message) {
        for(ServerThread t: list) {
            t.OnMessage(message);
        }
    }
    
    /**
     * Removes a thread with the given id from the list of active threads and puts it in
     * the list of finished threads
     * 
     * @param thread_id the id used to identify the thread
     */
    public synchronized void removeThread(long thread_id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == thread_id) {
                list.get(i).close(); // closing IO-streams
                trash.add(list.get(i)); // adds the thread to garbage list
                list.remove(i); // removes the thread from the active threads
            }
        }
    }
    
}
