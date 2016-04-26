package handshake;

import java.io.*;

/**
 * Finds the WebSocket key in the GET-request from the client
 * 
 * @author Aksel
 */
public class HSMessage {
    
    private BufferedReader br;
    
    public HSMessage(BufferedReader br) {
        this.br = br;
    }

    public BufferedReader getBr() {
        return br;
    }
    
    /**
     * Finds the key from the HTTP-get-Request from the client
     * 
     * @return the WebSocket key
     * @throws IOException if an IO-error occurs
     */
    public String findKey() throws IOException {
        String w = "Sec-WebSocket-Key"; // search-word
        CharSequence seq = w.subSequence(0,16); 
        String s = "";
        String key = "";
        while ((s = getBr().readLine()) != null && (!(s = getBr().readLine()).equals(""))) {
            if (s.contains(seq)) {
                key = getKey(s);
            }
        }
        return key;
    }
    
    /**
     * Retrieves the WebSocket key from the HTTP-headerline containing the key
     * @param hline the HTTP-headerline containing the key
     * @return the WebSocket key
     */
    public String getKey(String hline) {
        String key = hline.substring(19); // 19 is always the char-position where the key start in the headerline
        return key;
    }
}
