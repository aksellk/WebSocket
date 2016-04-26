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
    private MessageHandler handler;
    private Main main;
    
    public Handler(long id) {
        this.id = id;
        this.handler = new MessageHandler();
        this.main = new Main();
    }

    public Main getMain() {
        return main;
    }

    public MessageHandler getHandler() {
        return handler;
    }

    public long getId() {
        return id;
    }
    
    public void handle(Socket connection,OutputStream os) throws Exception {    
        
        try(InputStream is = connection.getInputStream()) {  

            boolean conn = true;
            //os.write(getHandler().getM().createPing()); // sends PING to client
            //getHandler().decodeMessage(is, os); // recieve and interpret PONG from client
            while (conn) {
                 conn = getHandler().decodeMessage(is,os);
                 byte[] message = getHandler().getMessage();
                 if (!conn) {
                     getMain().removeThread(getId());
                 }
                 if (message != null) {
                     getHandler().setMessage(null);
                     getMain().OnMessage(message); 
                 }
                 
            }
        }
    }
    
    public byte[] close() {
         MessageHandler handler = new MessageHandler();
         return handler.getM().createCloseMessage();
    }
}
