package server;

import handshake.Encoder;
import handshake.HSHandler;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import communication.Handler;
import java.util.ArrayList;

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
                                try(OutputStream os = connection.getOutputStream()) {
                                
                                HSHandler hshandler = new HSHandler();
                                String key = hshandler.findKey(br);
                                System.out.println(key);
                                Encoder encoder = new Encoder();
                                String encodedKey = encoder.createKey(key);
                                System.out.println(encodedKey);
                                
                                /* header lines from the server */
                                pw.println("HTTP/1.1 101 Switching Protocols");
                                pw.println("Upgrade: websocket");
                                pw.println("Connection: upgrade");
                                pw.println("Sec-WebSocket-Accept: " + encodedKey);  
                                pw.println(""); // End of headers
                                
                                
                                Handler handler = new Handler();
                                byte[] raw = null;
                                try(InputStream is = connection.getInputStream()) {  
                                    boolean conn = true;
                                    while (conn) {
                                    conn = handler.decodeMessage(is,os);
                                    //raw = handler.decodeMessage(is,os);
                                    //byte[] message = handler.sendMessage(raw);                             
                                    //os.write(message);
                                    pw.flush();
                                    }
                                }
                                }
                                
                                 
                                 //pw.flush();
                                 
                            }
                        }
                    }
                }
               
           
       }
        
    }
    
}
