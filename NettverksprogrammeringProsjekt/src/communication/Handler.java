package communication;

import java.io.*;
import java.net.Socket;
import server.Main;

/**
 *
 * @author Aksel
 */
public class Handler {
    
    private long id;
    
    public Handler(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    
    public void handle(Socket connection,OutputStream os) throws Exception {    
        
        try(InputStream is = connection.getInputStream()) {  
            MessageHandler handler = new MessageHandler();
            Main main = new Main();
            boolean conn = true;
            while (conn) {
                 conn = handler.decodeMessage(is,os);
                 byte[] message = handler.getMessage();
                 if (!conn) {
                     main.removeThread(getId());
                 }
                 if (message != null) {
                     handler.setMessage(null);
                     main.OnMessage(message); 
                 }
                 
            }
        }
    }
}
