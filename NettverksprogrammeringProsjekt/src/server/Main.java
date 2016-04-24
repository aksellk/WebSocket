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
    
    public static void main(String[] args) throws Exception {
        try(ServerSocket ss = new ServerSocket(80)) {
            ArrayList<Thread> list = new ArrayList<Thread>();
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
                for (Thread t : list) {
                    t.join();
                }
            }
        }
    }
    
}
