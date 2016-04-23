package mask;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class Handler {
        
    public byte[] decodeMessage(InputStream is) throws IOException {
        
        byte[] b1 = new byte[1];
        byte[] b2 = new byte[1];
        is.read(b1);
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
    
    public byte[] sendMessage(byte[] raw) {
        byte[] message = new byte[raw.length + 2];
        message[0] = (byte)129; //type of data
        int indexStartRawData = -1; // before set
        if(raw.length <= 125) {
            System.out.println("vanlig lengde");
            message[1] = (byte)raw.length;
            indexStartRawData = 2;
        }
        else if (raw.length >= 126 && raw.length <= 65535) {
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
