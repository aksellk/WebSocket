package communication;

import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Aksel
 */
public class MessageHandler {
    
    /* opcodes: */
    private static final int CONTINUATION_FRAME = 0;
    private static final int TEXT_FRAME = 1;
    private static final int CLOSE = 8;
    private static final int PONG = 10;
    
    private byte[] message = null;
    private Message m;
    
    public MessageHandler() {
        this.m = new Message();
    }

    public Message getM() {
        return m;
    }
    

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
    
    
        
    public boolean decodeMessage(InputStream is, OutputStream os) throws IOException {
        
        byte[] b1 = new byte[1];
        is.read(b1);
        byte firstByte = b1[0];
        byte fin = (byte) ((byte) (firstByte >> 7) & 0x1);
        int FIN = fin;
        byte opcode = (byte) (firstByte & 0xF);
        int oc = opcode;
        return handleMessage(is,os,oc);
        
    }
    
    public boolean handleMessage(InputStream is, OutputStream os,int opcode) throws IOException {
        byte[] raw = null;
        byte[] message = null;
        boolean conn = true;
        switch(opcode){
            case CONTINUATION_FRAME : // continuation frame
                System.out.println("continuation frame recieved");
                raw = decodeTextFrame(is);
                message = m.createMessage(raw,false);
                setMessage(message);
                break;
                
            case TEXT_FRAME : // text
                System.out.println("text frame recieved");
                raw = decodeTextFrame(is);
                message = m.createMessage(raw,true);
                //os.write(message);
                //os.write(createPing());
                setMessage(message);
                
                break;
            case CLOSE : // close
                System.out.println("close frame recieved");
                is.close();
                os.close();
                conn = false;
                break;
            
            case PONG : // recieves pong
                System.out.println("PONG recieved");
                break;
            default :
                break;
        }
        return conn;
    }
    
    
    
    
    
    public byte[] decodeTextFrame(InputStream is) throws IOException  {
        byte[] b2 = new byte[1];
        is.read(b2);
        
        byte secondbyte = b2[0];
        long length = secondbyte & 127;
        int indexFirstMask = 2; // normal case
        
        if (length == 126) { // 2 next bytes is 16bit unsigned int
            indexFirstMask = 4;
            byte[] b3 = new byte[2];
            is.read(b3);
            ByteBuffer buffer = ByteBuffer.wrap(b3);
            length = buffer.getShort();
            System.out.println("length: " + length);
        }
        if (length == 127) { // 8 next bytes is 64bit unsigned int
            indexFirstMask = 10; 
            byte[] b3 = new byte[8];
            is.read(b3);
            ByteBuffer buffer = ByteBuffer.wrap(b3);
            length = buffer.getShort();
        }
       
        // masks:
        byte[] masks = new byte[4];
        is.read(masks,0,4); // reads 4 bytes from index indexFirstMask
        
        // decoding:
        byte[] decoded = new byte[(int)length];
        byte[] bytes = new byte[(int)length];
        is.read(bytes);
         
        for (int i = 0; i < length; i++) {
            decoded[i] = (byte) (bytes[i]^masks[i%4]); // decoded = original XOR masking-key-octet at i MOD 4
        }
        String s = new String(decoded);
        System.out.println(s);
        return decoded;
    }
    
     
    
    
}
