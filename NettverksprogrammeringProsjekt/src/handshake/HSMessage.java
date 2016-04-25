package handshake;

import java.io.*;

/**
 *
 * @author Aksel
 */
public class HSMessage {
    
    public HSMessage() {}
    
    public String findKey(BufferedReader br) throws IOException {
        String w = "Sec-WebSocket-Key";
        CharSequence seq = w.subSequence(0,16);
        String s = "";
        String key = "";
        //int teller = 0;
        while ((s = br.readLine()) != null && (!(s = br.readLine()).equals(""))) {
            //teller++;
            //System.out.println(teller + " " + s);
            if (s.contains(seq)) {
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
