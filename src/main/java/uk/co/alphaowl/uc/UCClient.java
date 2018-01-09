package uk.co.alphaowl.uc;

import com.devtography.socket.javadotnet.ClientSocket;
import com.sun.istack.internal.Nullable;

import java.io.IOException;

/**
 * Public client class of the Universal Controller
 * library. Provides APIs access of the library.
 */
public class UCClient {

    private static volatile UCClient instance;

    private volatile boolean isRunning = false;

    private String remoteAddr;
    private int remotePort;
    private int bufferSize = 1024;

    private ClientSocket socket;

    private IUCCallbacks callback;

    private String player;
    private int playerId;

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
     * server address & port number. If <code>bufferSize
     * == -1</code>, default size 1024 will be used for
     * the size of the buffer.
     *
     * @param remoteAddr IPv4 address of the server
     * @param remotePort port number of the server
     * @param bufferSize size of the buffer for
     *                   message receive from server
     * @return instance of UCClient with
     * parameters set
     */
    public static UCClient init(final String remoteAddr, final int remotePort,
                                final int bufferSize)
            throws IOException {

        init();

        if (bufferSize != -1)
            instance.bufferSize = bufferSize;

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
     * @param remotePort port number
     * @throws IOException if an I/O error occurs when
     *                     connecting to the server
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
    public void disconnect() {
        try {
            socket.disconnect();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            disconnect();
        } finally {
            isRunning = false;
        }
    }

    /**
     * Set the listener/callback for the client.
     * @param listener an instance of <code>IUCCallbacks</code>
     */
    public void seetOnServerShuttedDownListener(IUCCallbacks listener) {
        callback = listener;
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
        while (isRunning) {
            try {
                socket.readString(bufferSize, UCCommand.DELIMITER);
            } catch (IOException ex) {
                disconnect();
            }
        }
    }

    private void onMsgReceived(String msg) {
        String[] decodedString = msg.split(UCCommand.SEPRARTOR);

        switch (decodedString.length) {
            case 1:
                switch (decodedString[0]) {
                    case UCCommand.SERVER_SHUTDOWN:

                    case UCCommand.PLAYER_NOT_FOUND:
                    case UCCommand.INVALID_CMD:
                }
            case 2:
                if (decodedString[0].equals(UCCommand.PLAYER_ID))
                    // Player ID received from server
                    playerId = Integer.parseInt(decodedString[1]);
                break;
        }
    }
}
