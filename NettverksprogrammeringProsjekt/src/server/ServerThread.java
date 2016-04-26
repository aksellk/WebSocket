package server;

import message.Handler;
import handshake.HSHandler;
import java.io.BufferedReader;
import java.io.*;
import java.net.Socket;

/**
 *
 * @author Aksel
 */
public class ServerThread extends Thread {
    private Socket connection;
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw; 
    private OutputStream os;
    private HSHandler hshandler;
    private Handler handler;
    
    public ServerThread(Socket connection) {
        this.connection = connection;
        this.hshandler = new HSHandler();
        this.handler = new Handler(this.getId());
    }

    public HSHandler getHshandler() {
        return hshandler;
    }

    public Handler getHandler() {
        return handler;
    }


    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public InputStreamReader getIsr() {
        return isr;
    }

    public void setIsr(InputStreamReader isr) {
        this.isr = isr;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public OutputStream getOs() {
        return os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }
    
    
    
    @Override
    public void run() {
        try {
            /* open connections */
            open();
            /* Handle Communication */
            handle();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* close connections */
            close();
        }
        
    }
    
    public void handle() throws Exception {
        try {
            /* Handshake */
            getHshandler().handle(getBr(), getPw());   

            /* Handle messages */
            getHandler().handle(getConnection());
        
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    public void OnMessage(byte[] message) {
        try {
            getOs().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void OnClose() {
        try {
            byte[] close = getHandler().close();
            getOs().write(close);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        
        
    }

     public void open() {
        try {
            setIsr(new InputStreamReader(getConnection().getInputStream()));
            setBr(new BufferedReader(getIsr()));
            setPw(new PrintWriter(getConnection().getOutputStream(), true));
            setOs(getConnection().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
     
   
    
    public void close() {
        try {
            getPw().close();
            getBr().close();
            getIsr().close();
            getOs().close();
            getIsr().close();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
    
}
