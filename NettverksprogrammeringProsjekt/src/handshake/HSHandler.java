
package handshake;

import java.io.*;
/**
 * Handles the handshake with the client when the communication has started
 * 
 * @author Aksel
 */
public class HSHandler {
    
    /**
     * Decodes the HTTP-GET-request from the client and
     * sends the HTTP 101 headerlines as response
     * 
     * @param br BufferedWriter used to interpret the request from the client
     * @param pw Printwriter used to send the response
     */
    public void handle(BufferedReader br, PrintWriter pw) {
        try {
            HSMessage hsmessage = new HSMessage(br);
            /* Finds the WebSocket key from the clients GET-request: */
            String key = hsmessage.findKey();
            System.out.println(key);
            /* Encodes the WebSocket key using base64 and SHA-1: */
            Encoder encoder = new Encoder();
            String encodedKey = encoder.createKey(key);
            System.out.println(encodedKey);

            /* header lines from the server */
            pw.println("HTTP/1.1 101 Switching Protocols");
            pw.println("Upgrade: websocket");
            pw.println("Connection: upgrade");
            pw.println("Sec-WebSocket-Accept: " + encodedKey);  
            pw.println(""); // End of headers
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
