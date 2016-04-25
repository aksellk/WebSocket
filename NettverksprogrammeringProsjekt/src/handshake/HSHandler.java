
package handshake;

import java.io.*;
/**
 *
 * @author Aksel
 */
public class HSHandler {
    
    public void handle(BufferedReader br, PrintWriter pw) {
        try {
            HSMessage hshandler = new HSMessage();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
