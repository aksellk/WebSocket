package mask;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class Handler {
        
    public void messageHandler(InputStream is) throws IOException {
        
        //byte[] b = new byte[is.available()];
        //is.read(b);
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
        if (length == 127) indexFirstMask = 10; // 8 next bytes is 64bit unsigned int
       
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
    }
    
}
