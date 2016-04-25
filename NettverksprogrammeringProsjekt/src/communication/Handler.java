package communication;

import java.io.*;
import java.net.Socket;
import server.Main;

/**
 *
 * @author Aksel
 */
public class Handler {
    
    public void handle(Socket connection,OutputStream os) throws Exception {
        MessageHandler handler = new MessageHandler();
        Main main = new Main();
        try(InputStream is = connection.getInputStream()) {  
            boolean conn = true;
            while (conn) {
                 conn = handler.decodeMessage(is,os);
                 byte[] message = handler.getMessage();
                 if (message != null) {
                     handler.setMessage(null);
                     main.OnMessage(message); 
                 }
            }
        }
    }
}
