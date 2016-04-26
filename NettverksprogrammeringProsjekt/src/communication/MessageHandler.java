package communication;

import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Aksel
 */
public class MessageHandler {
    
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
        //byte o = (byte) (firstByte & 00001111);
        byte opcode = (byte) (firstByte & 0xF);
        int oc = opcode;
        return handleMessage(is,os,oc);
        
    }
    
    public boolean handleMessage(InputStream is, OutputStream os,int opcode) throws IOException {
        boolean conn = true;
        switch(opcode){
            case 0 : // continuation frame
                System.out.println("continuation frame");
                conn = true;
                break;
                
            case 1 : // text
                
                byte[] raw = decodeTextFrame(is);
                byte[] message = m.createMessage(raw);
                //os.write(message);
                //os.write(createPing());
                setMessage(message);
                
                break;
            case 8 : // close
                is.close();
                //byte[] close = createCloseMessage();
                //os.write(close);
                os.close();
                conn = false;
                break;
            
            case 10 : // recieves pong
                System.out.println("PONG!");
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
        //int indexFirstDataByte = indexFirstMask + 4; // index of first data
        //int len = length - indexFirstDataByte; // length of payload
       
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
