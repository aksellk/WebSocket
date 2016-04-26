package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 *
 * @author Aksel
 */
public class Main {
    
    public static ArrayList<ServerThread> list;
    public static ArrayList<ServerThread> trash;
    
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
                    t.OnClose(); // sender closing-handshake
                    t.close();
                    t.join();
                }
                for (ServerThread t : trash) {
                    t.join();
                }
            }
        }
    }
    
    public synchronized void OnMessage(byte[] message) {
        for(ServerThread t: list) {
            t.OnMessage(message);
        }
    }
    
    public synchronized void removeThread(long thread_id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == thread_id) {
                list.get(i).close(); // lukker alle forbindelser
                trash.add(list.get(i)); // legger til i søppel
                list.remove(i); // fjerner fra aktive tråder
            }
        }
    }
    
}
