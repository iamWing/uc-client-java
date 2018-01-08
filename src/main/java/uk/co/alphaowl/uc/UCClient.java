package uk.co.alphaowl.uc;

import com.devtography.socket.javadotnet.ClientSocket;

import java.io.IOException;

/**
 * Public client class of the Universal Controller
 * library. Provides APIs access of the library.
 */
public class UCClient {

    private static volatile UCClient instance;

    private String remoteAddr;
    private int remotePort;

    private ClientSocket socket;

    private String player;

    /**
     * Default constructor.
     */
    private UCClient() { }

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
     */
    public void connect() throws IOException {

        socket.connect();
    }

    /**
     * Deregisters the player from server and closes
     * the connection.
     */
    public void disconnect() throws IOException {

        socket.disconnect();
    }

    /**
     * Sends a key down action to the server.
     *
     * @param key identifier of the key/button
     */
    public void keyDown(String key) throws IOException {

        StringBuilder msg = new StringBuilder();

        socket.writeString(msg.toString(), null);
    }

    /* Getters */

    /**
     * @return the instance that currently pointing to
     */
    public UCClient getInstance() {
        return instance;
    }

    /**
     * @return the IPv4 address of the server connected
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * @return the port number of the server connected
     */
    public int getRemotePort() {
        return remotePort;
    }
}
