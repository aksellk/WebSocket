
package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author Aksel
 */
public class Encoder {
    
    /**
     * TODO:
     * 
     * - key skal bestå av nøkkel fra klient + magisk tall (spør Arild)
     */
    
    public Encoder() {}
    
    public String createKey(String key) {
             MessageDigest md = null;
             String encodedKey = "test";
            try {
                md = MessageDigest.getInstance("SHA1");
                encodedKey = Base64.getEncoder().encodeToString(md.digest(key.getBytes()));
            }
            catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            
        return encodedKey;
    }
    
 
    
}
