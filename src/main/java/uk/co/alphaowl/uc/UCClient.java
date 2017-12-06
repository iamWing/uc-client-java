package uk.co.alphaowl.uc;

import com.devtography.socket.javadotnet.ClientSocket;

import java.io.IOException;

/**
 * Public client class of the Universal Controller
 * library. Provides APIs access of the library.
 */
public class UCClient {

    private static volatile UCClient instance;

    private static final String DELIMITER = "<EOM>";
    private static final String SEPARATOR = ";";

    private static final String TAG_REGISTER = "Register:";
    private static final String TAG_DEREGISTER = "Deregister:";
    private static final String TAG_PLAYER = "Player:";
    private static final String TAG_KEYDOWN = "KeyDown:";

    private String remoteAddr;
    private int remotePort;

    private ClientSocket socket;

    private String player;

    /**
     * Default constructor.
     */
    private UCClient() {
    }

    /**
     * Constructor with predefine server IP address &
     * port number.
     *
     * @param remoteAddr IPv4 address of the server
     * @param remotePort port number of the server
     */
    private UCClient(final String remoteAddr, final int remotePort) {
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
    }

    /**
     * Static initialisation method of UCClient. This
     * is the default method to initial an new instance.
     *
     * @return instance of UCClient
     */
    public static UCClient init() {

        // lazy initialisation
        if (instance == null) {

            // utilising DCL idiom to ensure only
            // one instance is being created
            synchronized (UCClient.class) {

                // must check again as one of the blocked
                // threads can still enter this block
                if (instance == null)
                    instance = new UCClient(); // safe
            } // end synchronized
        } // end if

        return instance;
    }

    /**
     * Alternative static init method of UCClient with
     * server address & port number.
     *
     * @param remoteAddr IPv4 address of the server
     * @param remotePort port number of the server
     * @return instance of UCClient with
     * parameters set
     */
    public static UCClient init(final String remoteAddr,
                                final int remotePort) {

        init();

        instance.remoteAddr = remoteAddr;
        instance.remotePort = remotePort;

        instance.socket = ClientSocket.init(remoteAddr, remotePort);
        instance.socket.setOnConnectionCreatedListener(
                () -> instance.playerRegister());

        return instance;
    }

    /**
     * Reset the static instance reference to null
     * in case there is any need for an new instance.
     */
    public void destroy() {
        instance = null;
    }

    /**
     * Connects to socket server.
     *
     * @param player name of the player.
     */
    public void connect(String player) {

        this.player = player;

        try {
            socket.connect();
        } catch (IOException ex) {
            // Todo - create connection fail listener
        }
    }

    /**
     * Deregisters the player from server and closes
     * the connection.
     */
    public void disconnect() {
        try {
            socket.writeString(TAG_DEREGISTER + player + DELIMITER,
                    null);
            socket.disconnect();
        } catch (IOException ex) {

        }
    }

    /**
     * Sends a key down action to the server.
     *
     * @param key identifier of the key/button
     */
    public void keyDown(String key) {

        StringBuilder msg = new StringBuilder();
        msg.append(TAG_PLAYER).append(player).append(SEPARATOR);
        msg.append(TAG_KEYDOWN).append(key);
        msg.append(DELIMITER);

        try {
            socket.writeString(msg.toString(), null);
        } catch (IOException ioe) {

        }
    }

    /* Getters */

    /**
     * @return the instance that currently pointing to
     */
    public UCClient getInstance() {
        return instance;
    }

    /* Private methods */

    /**
     * Register the player to server.
     */
    private void playerRegister() {
        try {
            socket.writeString(TAG_REGISTER + player + DELIMITER,
                    null);
        } catch (IOException ex) {

        }
    }
}
