package server;

import communication.Handler;
import handshake.Encoder;
import handshake.HSHandler;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Aksel
 */
public class ServerThread extends Thread {
    private Socket connection;
    
    public ServerThread(Socket connection) {
        this.connection = connection;
    }
    
    @Override
    public void run() {
        try {
            handle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void handle() throws Exception {
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
