package handshake;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class Handler {
    
    public Handler() {}
    
    public String findKey(BufferedReader br) throws IOException {
        String s = "";
        String key = "";
        int teller = 0;
        while ((s = br.readLine()) != null) {
            teller++;
            System.out.println(teller + " " + s);
            if (teller == 12) { // nøkkel
                key = s;
            }
        }
        return getKey(key);
    }
    
    public String getKey(String hline) {
        String key = hline.substring(19);
        return key;
    }
}
