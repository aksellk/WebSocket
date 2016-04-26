package communication;

/**
 *
 * @author Aksel
 */
public class Message {
    
    public byte[] createPing() {
        byte[] ping = new byte[2];
        //ping[0] = 0x9;
        ping[0] = (byte) 10001001;
        ping[1] = 0;
        return ping;
    }
    
    public byte[] createMessage(byte[] raw) {
        byte[] message = null;
        int indexStartRawData = -1; // before set
        
        if(raw.length <= 125) { // vanlig lengde
            System.out.println("vanlig lengde");
            message = new byte[raw.length + 2];
            message[0] = (byte)129; //type of data
            message[1] = (byte)raw.length;
            indexStartRawData = 2;
        }
        else if (raw.length >= 126 && raw.length <= 65535) { //spesialtilfelle 1
            System.out.println("lengde 16bit unsigned int");
            message = new byte[raw.length + 4];
            message[0] = (byte)129; //type of data
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
        int code = 1001;
        close[0] = (byte) code;
        return close;
    }
    
}
