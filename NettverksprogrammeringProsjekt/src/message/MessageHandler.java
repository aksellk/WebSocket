package message;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Handles messages from the client and produces responses
 * 
 * @author Aksel
 */
public class MessageHandler {
    
    /* opcodes: */
    private static final int CONTINUATION_FRAME = 0;
    private static final int TEXT_FRAME = 1;
    private static final int CLOSE = 8;
    private static final int PONG = 10;
    
    private byte[] message = null; // contains a message if a message will get sent 
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
    
    
    /**
     * Decodes a message from the client and give a response based on the message:
     * Splits the first byte of the message into FIN-flag and opcode
     * 
     * @param is InputStream that contains the message from the client
     * @return 
     *      false if a close-frame is recieved
     *      true if a close-frame is not recieved
     * @throws IOException 
     */  
    public boolean decodeMessage(InputStream is) throws IOException {     
        byte[] b1 = new byte[1]; 
        is.read(b1); // reads the first byte of the message
        byte firstByte = b1[0];
        byte fin = (byte) ((byte) (firstByte >> 7) & 0x1); // retrieve the FIN-flag which is the MSB
        int FIN = fin;
        byte opcode = (byte) (firstByte & 0xF); // retrieve the opcode which is the four LSBs
        int oc = opcode;
        return handleMessage(is,oc);     
    }
    
    /**
     * Handles what to to based on the given opcode:
     * 
     * If a continuation-frame is recieved the the message field is set with a byte-array where
     * the FIN-flag is not set, the opcode is continuation-frame, 
     * the length is the length of the payload from the client and the payload is the payload from the client
     * and the return value is true.
     * 
     * If a text-frame is recieved the the message field is set with a byte-array where
     * the FIN-flag is set, the opcode is text-frame, 
     * the length is the length of the payload from the client and the payload is the payload from the client
     * and the return value is true.
     * 
     * If a close-frame is recieved the connection is closed and the return value is false
     * 
     * If a pong-frame is recieved the return value is true
     * 
     * @param is InputStream which contains the message from the client
     * @param opcode the opcode which speciefies the type of message from the client
     * @return false if a close-frame is recieved, otherwise true
     * @throws IOException 
     */
    public boolean handleMessage(InputStream is,int opcode) throws IOException {
        byte[] raw = null;
        byte[] message = null;
        boolean conn = true;
        switch(opcode){
            case CONTINUATION_FRAME : // continuation frame
                System.out.println("continuation frame recieved");
                raw = decodeTextFrame(is); // the payload
                message = m.createMessage(raw,false); // creates a new message to the client with continuation
                setMessage(message); // sets the message in the message field
                break;
                
            case TEXT_FRAME : // text
                System.out.println("text frame recieved");
                raw = decodeTextFrame(is); // the payload
                message = m.createMessage(raw,true); // creates a new message to the client without continuation
                setMessage(message); // sets the message in the message field   
                break;
            case CLOSE : // close
                System.out.println("close frame recieved");
                is.close();
                conn = false; // sets the return-value to false
                break;
            
            case PONG : // recieves pong
                System.out.println("PONG recieved");
                break;
            default :
                break;
        }
        return conn;
    }
    
    
    
    
    /**
     * Decodes the the frame recieved from the client except the opcode and FIN-flag which is already decoded.
     * Finds the length of the payload and unmasks the payload
     * @param is InputStream which contains the message from the client
     * @return a byte-array containing the payload
     * @throws IOException 
     */
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
       
        /* masks: */
        byte[] masks = new byte[4];
        is.read(masks,0,4); // reads 4 bytes from index indexFirstMask
        
        /* decoding: */
        byte[] decoded = new byte[(int)length];
        byte[] bytes = new byte[(int)length];
        is.read(bytes);
        /* unmasking the payload from the client: */ 
        for (int i = 0; i < length; i++) {
            decoded[i] = (byte) (bytes[i]^masks[i%4]); // decoded = original XOR masking-key-octet at i MOD 4
        }
        String s = new String(decoded);
        System.out.println(s);
        return decoded;
    } 
    
}
