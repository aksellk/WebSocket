package nettverksprogrammeringprosjekt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aksel
 */
public class Server {

    public static void main(String[] args) throws IOException {
        try(ServerSocket ss = new ServerSocket(80)) {
            
                try(Socket connection = ss.accept()) {
                    try(InputStreamReader isr = new InputStreamReader(connection.getInputStream())) {
                        try(BufferedReader br = new BufferedReader(isr)) {
                            try(PrintWriter pw = new PrintWriter(connection.getOutputStream(), true)) {
                            
                                 
                                 pw.flush();
                            }
                        }
                    }
                }
               
           
       }
        
    }
    
}
