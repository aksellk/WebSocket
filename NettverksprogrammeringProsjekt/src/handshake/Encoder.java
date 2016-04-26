
package handshake;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Encodes the WebSocket key using base64 and SHA-1
 * 
 * @author Aksel
 */
public class Encoder {
    
    final static String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"; // magic word
    
    public Encoder() {}
    
    /**
     * Encodes the given key unsing base64 and the SHA-1 hashing-algorithm
     * 
     * @param key
     * @return 
     */
    public String createKey(String key) {
             MessageDigest md = null;
             String encodedKey = key + GUID;
            try {
                md = MessageDigest.getInstance("SHA1");
                /* Encoding and hashing: */
                encodedKey = Base64.getEncoder().encodeToString(md.digest(encodedKey.getBytes()));
            }
            catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            
        return encodedKey;
    }
    
}
