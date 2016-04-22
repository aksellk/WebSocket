package handshake;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class HSHandler {
    
    public HSHandler() {}
    
    public String findKey(BufferedReader br) throws IOException {
        String s = "";
        String key = "";
        int teller = 0;
        //while ((s = br.readLine()) != null) {
        for (int i = 0; i < 14; i++) {
            s = br.readLine();
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
