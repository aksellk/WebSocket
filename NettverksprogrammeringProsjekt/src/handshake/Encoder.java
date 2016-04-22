
package handshake;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author Aksel
 */
public class Encoder {
    
    
    final static String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    
    public Encoder() {}
    
    public String createKey(String key) {
             MessageDigest md = null;
             String encodedKey = key + GUID;
            try {
                md = MessageDigest.getInstance("SHA1");
                encodedKey = Base64.getEncoder().encodeToString(md.digest(encodedKey.getBytes()));
            }
            catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            
        return encodedKey;
    }
    
}
