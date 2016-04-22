package server;

import handshake.Encoder;
import handshake.Handler;
import java.io.*;
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
                                
                                Handler handler = new Handler();
                                String key = handler.findKey(br);
                                System.out.println(key);
                                Encoder encoder = new Encoder();
                                String encodedKey = encoder.createKey(key);
                                System.out.println(encodedKey);
                                
                                pw.println("HTTP/1.1 101 Switching Protocols");
                                pw.println("Upgrade: websocket");
                                pw.println("Connection: upgrade");
                                pw.println("Sec-WebSocket-Accept: " + encodedKey);
                                
                                pw.println(""); // End of headers
                                 
                                 pw.flush();
                            }
                        }
                    }
                }
               
           
       }
        
    }
    
}
