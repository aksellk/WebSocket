package communication;

/**
 *
 * @author Aksel
 */
public class Message {
    
    private final byte FIN_TEXT = (byte) 0x81;  // 10000001
    private final byte FIN_CLOSE = (byte) 0x88; // 10001000
    private final byte FIN_PING = (byte) 0x89;  // 10001001

    
    public byte[] createPing() {
        byte[] ping = new byte[2];
        //ping[0] = 0x9;
        ping[0] = FIN_PING;
        ping[1] = 0;
        return ping;
    }
    
    public byte[] createMessage(byte[] raw) {
        byte[] message = null;
        int indexStartRawData = -1; // before set
        
        if(raw.length <= 125) { // vanlig lengde
            System.out.println("vanlig lengde");
            message = new byte[raw.length + 2];
            message[0] = FIN_TEXT; 
            message[1] = (byte)raw.length;
            indexStartRawData = 2;
        }
        else if (raw.length >= 126 && raw.length <= 65535) { //spesialtilfelle 1
            System.out.println("lengde 16bit unsigned int");
            message = new byte[raw.length + 4];
            message[0] = FIN_TEXT; //type of data
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
    
    
    public byte[] createCloseMessage() {
        byte[] close = new byte[1];
        close[0] = FIN_CLOSE;
        return close;
    }
    
}
