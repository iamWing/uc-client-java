package uk.co.alphaowl.uc;

import com.devtography.socket.javadotnet.ClientSocket;

import java.io.IOException;

/**
 * Public client class of the Universal Controller
 * library. Provides APIs access of the library.
 */
public class UCClient {

    private static volatile UCClient instance;

    private boolean isRunning = false;

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
                                final int remotePort) throws IOException {

        init();

        instance.connect(remoteAddr, remotePort);

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
     * Connects to the server with host's IP address
     * and port number provided.
     *
     * @param remoteAddr host's IP address.
     * @param remotePort port number.
     * @throws IOException if an I/O error occurs when
     *                     connecting to the server.
     */
    public void connect(final String remoteAddr, final int remotePort)
            throws IOException {
        if (!isRunning) {
            isRunning = true;

            this.remoteAddr = remoteAddr;
            this.remotePort = remotePort;

            try {
                socket = ClientSocket.init(remoteAddr, remotePort);
                socket.setOnConnectionCreatedListener(this::onConnected);
                socket.setOnReadStringCompleteListener(this::onMsgReceived);
                socket.connect();
            } catch (IOException ex) {
                isRunning = false;

                throw ex;
            }
        }
    }


    /**
     * Deregister the player from server and closes
     * the connection.
     */
    public void disconnect() throws IOException {

        socket.disconnect();

        isRunning = false;
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

    /* Private methods */

    private void onConnected() {

    }

    private void onMsgReceived(String msg) {

    }
}
