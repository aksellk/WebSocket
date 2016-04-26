package message;

import java.io.*;
import java.net.Socket;
import server.Main;

/**
 * Handles communication with the client
 * 
 * @author Aksel
 */
public class Handler {
    
    private long id; // thread_id
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
    
    /**
     * Handles communication with the client:
     * 
     * If a close-frame is recieved from the client; the connection closes 
     * and the thread is taken out of the active threads
     * 
     * If a message is recieved from the client, a response-frame is produced and gets sent to all clients
     * the message field is then set to null 
     * 
     * @param connection The socket which contains the InputStream with the messages from the client
     * @throws Exception 
     */
    public void handle(Socket connection) throws Exception {    
        
        try(InputStream is = connection.getInputStream()) {  

            boolean conn = true;
            //os.write(getHandler().getM().createPing()); // sends PING to client
            //getHandler().decodeMessage(is, os); // recieve and interpret PONG from client
            while (conn) {
                 conn = getHandler().decodeMessage(is);
                 byte[] message = getHandler().getMessage();
                 
                 if (!conn) getMain().removeThread(getId());
  
                 if (message != null) {
                     getHandler().setMessage(null);
                     getMain().OnMessage(message); 
                 }
                 
            }
        }
    }
    
    /**
     * Creates a close-message which for the client when the server takes initiative to disconnect
     * @return the close-frame
     */
    public byte[] close() {
         MessageHandler handler = new MessageHandler();
         return handler.getM().createCloseMessage();
    }
}
