package communication;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class MessageHandler {
    
    private byte[] message = null;

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
        
    //public byte[] decodeMessage(InputStream is, OutputStream os) throws IOException {
    public boolean decodeMessage(InputStream is, OutputStream os) throws IOException {
        
        byte[] b1 = new byte[1];
        is.read(b1);
        byte firstByte = b1[0];
        byte fin = (byte) ((byte) (firstByte >> 7) & 0x1);
        //byte o = (byte) (firstByte & 00001111);
        byte opcode = (byte) (firstByte & 0xF);
        int oc = opcode;
        return handleMessage(is,os,oc);
        
    }
    
    public boolean handleMessage(InputStream is,OutputStream os,int opcode) throws IOException {
        boolean conn = true;
        switch(opcode){
            case 0 : // contuation frame
                conn = true;
                break;
                
            case 1 : // text
                byte[] raw = decodeTextFrame(is);
                byte[] message = createMessage(raw);
                //os.write(message);
                setMessage(message);
                break;
            case 8 : // close
                is.close();
                //byte[] close = createCloseMessage();
                //os.write(close);
                os.close();
                conn = false;
                break;
            case 9 : //ping
                
                break;
            case 10 : // pong
                break;
            default :
                break;
        }
        return conn;
    }
    /*
    public byte[] createCloseMessage() {
        byte[] close = new byte[1];
        int code = 1001;
        close[0] = (byte) code;
        return close;
    }*/
    
    public byte[] decodeTextFrame(InputStream is) throws IOException  {
        byte[] b2 = new byte[1];
        is.read(b2);
        
        byte secondbyte = b2[0];
        int length = secondbyte & 127;
        int indexFirstMask = 2; // normal case
        
        if (length == 126) { // 2 next bytes is 16bit unsigned int
            indexFirstMask = 4;
            byte[] b3 = new byte[2];
            is.read(b3);
        }
        if (length == 127) { // 8 next bytes is 64bit unsigned int
            indexFirstMask = 10; 
            byte[] b3 = new byte[8];
            is.read(b3);
        }
       
        // masks:
        byte[] masks = new byte[4];
        is.read(masks,0,4); // reads 4 bytes from index indexFirstMask
        
        // decoding:
        //int indexFirstDataByte = indexFirstMask + 4; // index of first data
        //int len = length - indexFirstDataByte; // length of payload
       
        byte[] decoded = new byte[length];
        byte[] bytes = new byte[length];
        is.read(bytes);
         
        for (int i = 0; i < length; i++) {
            decoded[i] = (byte) (bytes[i]^masks[i%4]); // decoded = original XOR masking-key-octet at i MOD 4
        }
        String s = new String(decoded);
        System.out.println(s);
        return decoded;
        
    }
        
    
    public byte[] createMessage(byte[] raw) {
        byte[] message = new byte[raw.length + 2];
        message[0] = (byte)129; //type of data
        int indexStartRawData = -1; // before set
        
        if(raw.length <= 125) { // vanlig lengde
            System.out.println("vanlig lengde");
            message[1] = (byte)raw.length;
            indexStartRawData = 2;
        }
        else if (raw.length >= 126 && raw.length <= 65535) { //spesialtilfelle 1
            System.out.println("lengde 16bit unsigned int");
            message[1] = 126;
            message[2] = (byte) ((raw.length >> 8) & 255);
            message[3] = (byte) ((raw.length) & 255);
            indexStartRawData = 4;
        }
        
        int j = indexStartRawData;
        for (int i = 0; i < raw.length; i++) {
            message[j] = raw[i];
            j++;
        }
        String s = new String(message);
        System.out.println(s);
        return message;
    }
    
}
