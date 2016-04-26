package message;

/**
 * Produces full-messages which will get sent to the server
 * 
 * @author Aksel
 */
public class Message {
    /* The different first byte of the message */
    private final byte FIN_TEXT = (byte) 0x81;   // 10000001 FIN and TEXT-FRAME
    private final byte FIN_CLOSE = (byte) 0x88;  // 10001000 FIN and CLOSE-FRAME
    private final byte FIN_PING = (byte) 0x89;   // 10001001 FIN and PING-FRAME
    private final byte CONTINUATION = (byte) 0;  // 00000000 NOT FIN and CONTINUATION-FRAME

    /**
     * Creates a new message with FIN-flag, opcode, length and payload:
     * 
     * If the length of the payload is less than 125 the second byte of the message is the length of the payload
     * 
     * If the length of the payload is between 126 and 65535 the second and third byte is the length of the payload
     * 
     * @param raw the payload
     * @param 
     *      - true if the FIN-flag is set
     *      - false if the FIN-flag is not set
     * @return the message which will get sent to the client
     */
    public byte[] createMessage(byte[] raw, boolean fin) {
        byte[] message = null;
        int indexStartRawData = -1; // before set
        
        if(raw.length <= 125) { // normal length
            message = new byte[raw.length + 2];
            if (fin) message[0] = FIN_TEXT; // FIN-flag set and text-frame opcode
            else message[0] = CONTINUATION; // FIN-flag not set and continuation opcode
            message[1] = (byte)raw.length;  // length of the payload
            indexStartRawData = 2;
        }
        else if (raw.length >= 126 && raw.length <= 65535) { // special case 1
            message = new byte[raw.length + 4];
            if (fin) message[0] = FIN_TEXT; // FIN-flag set and text-frame opcode
            else message[0] = CONTINUATION; // FIN-flag not set and continuation opcode
            message[1] = 126;
            /* length as 16-bit unsigned int: */
            message[2] = (byte) ((raw.length >> 8) & 255); 
            message[3] = (byte) ((raw.length) & 255);
            indexStartRawData = 4;
        }     
        /* copying payload: */
        int j = indexStartRawData;
        for (int i = 0; i < raw.length; i++) {
            message[j] = raw[i];
            j++;
        }
        String s = new String(message);
        System.out.println(s);
        return message;
    } 
    
    /**
     * Creates a close-message which will get sent if the server takes initiative to closing the connection
     * @return the close message
     */
    public byte[] createCloseMessage() {
        byte[] close = new byte[2];
        close[0] = FIN_CLOSE; // FIN-flag set and close opcode
        close[1] = 0; // the length = 0
        return close;
    }
    
    /**
     * Creates a ping message to send to the client
     * @return the ping message
     */
    public byte[] createPing() {
        byte[] ping = new byte[2];
        ping[0] = FIN_PING; // FIN-flag set and ping opcode
        ping[1] = 0; // the length = 0
        return ping;
    }
    
}
