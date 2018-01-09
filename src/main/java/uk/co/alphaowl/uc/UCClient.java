package uk.co.alphaowl.uc;

import com.devtography.socket.javadotnet.ClientSocket;
import org.jetbrains.annotations.Nullable;
import uk.co.alphaowl.uc.exceptions.PlayerNotRegisteredException;
import uk.co.alphaowl.uc.exceptions.PlayerRegisteredException;

import java.io.IOException;

/**
 * Public client class of the Universal Controller
 * library. Provides APIs access of the library.
 */
public class UCClient {

    private static volatile UCClient instance;

    private volatile boolean isRunning = false;

    private IUCCallback callback;

    private Thread workerThread;

    private String remoteAddr;
    private int remotePort;
    private int bufferSize = 1024;

    private ClientSocket socket;

    private String player;
    private int playerId = -1;

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
                                final int bufferSize, IUCCallback callback)
            throws IOException {

        init();

        if (bufferSize != -1)
            instance.bufferSize = bufferSize;

        instance.callback = callback;

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
        isRunning = false;

        try {
            socket.disconnect();
            workerThread.interrupt();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            disconnect();
        }
    }

    /* Commands */

    /**
     * Register a new player to server.
     *
     * @param playerName name of the player
     * @throws PlayerRegisteredException if a player has already
     *                                   been registered from
     *                                   this device
     */
    public void register(String playerName) throws PlayerRegisteredException {
        if (playerId == -1) {
            player = playerName;
            String cmd = UCCommand.registerCmd(playerName);

            sendCmd(cmd);
        } else throw new PlayerRegisteredException();
    }

    /**
     * Deregister the player from server.
     *
     * @throws PlayerNotRegisteredException if there is no
     *                                      player ID received
     *                                      from the server yet
     */
    public void deregister() throws PlayerNotRegisteredException {
        if (playerId != -1) {
            String cmd = UCCommand.deregisterCmd(playerId);
            sendCmd(cmd);
        } else throw new PlayerNotRegisteredException();
    }

    /**
     * Sends a key down action to the server.
     *
     * @param key   identifier of the key/button
     * @param extra optional extra content
     * @throws PlayerNotRegisteredException if there is no
     *                                      player ID received
     *                                      from the server yet
     */
    public void keyDown(String key, @Nullable String extra)
            throws PlayerNotRegisteredException {

        if (playerId != -1) {
            String cmd;

            if (extra != null)
                cmd = UCCommand.keyDownCmd(
                        playerId, new String[]{
                                key, extra.replace(UCCommand.SEPRARTOR,
                                "")
                        }
                );
            else cmd = UCCommand.keyDownCmd(playerId, new String[]{key});

            sendCmd(cmd);
        } else {
            throw new PlayerNotRegisteredException();
        }
    }

    /**
     * Sends a joystick command to the server. Value x & y
     * must be smaller or equal 1.0f and larger or equal -1.0f.
     *
     * @param x x location of the joystick
     * @param y y location of the joystick
     * @throws PlayerNotRegisteredException if there is no
     *                                      player ID received
     *                                      from the server yet
     */
    public void joystick(float x, float y) throws PlayerNotRegisteredException {
        if (playerId != -1) {
            if (x <= -1.0f || x >= 1.0f
                    || y <= -1.0f || y >= 1.0f)
                illegalFloatValue();
            else {
                String cmd = UCCommand.joystickCmd(playerId, x, y);
                sendCmd(cmd);
            }
        } else throw new PlayerNotRegisteredException();
    }

    /**
     * Sends a gyro command to the server. Value x, y & z
     * must be smaller or equal 1.0f and larger or equal -1.0f.
     *
     * @param x x value of the gyro
     * @param y y value of the gyro
     * @param z z value of the gyro
     * @throws PlayerNotRegisteredException if there is no
     *                                      player ID received
     *                                      from the server yet
     */
    public void gyro(float x, float y, float z)
            throws PlayerNotRegisteredException {
        if (playerId != -1) {
            String cmd = UCCommand.gyroCmd(playerId, x, y, z);
            sendCmd(cmd);
        } else throw new PlayerNotRegisteredException();
    }

    /* Setters */

    /**
     * Set the listener/callback for the client.
     *
     * @param listener an instance of <code>IUCCallback</code>
     */
    public void setCallback(IUCCallback listener) {
        callback = listener;
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

    public String getPlayerName() {
        return player;
    }

    public int geetPlayerId() {
        return playerId;
    }

    /* Private methods */

    private void onConnected() {
        workerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && isRunning) {
                try {
                    socket.readString(bufferSize, UCCommand.DELIMITER);
                } catch (IOException ex) {
                    disconnect();
                }
            }
        });
        workerThread.start();
    }

    private void onMsgReceived(String msg) {
        String[] decodedString = msg.split(UCCommand.SEPRARTOR);

        switch (decodedString.length) {
            case 1:
                switch (decodedString[0]) {
                    case UCCommand.SERVER_FULL:
                        callback.onServerFull();
                        disconnect();
                        break;
                    case UCCommand.SERVER_SHUTDOWN:
                        callback.onServerDisconnected();
                        break;
                    case UCCommand.PLAYER_NOT_FOUND:
                        callback.onPlayerNotFound();
                        break;
                    case UCCommand.INVALID_CMD:
                        callback.invalidCmd();
                        break;
                    default:
                        onInvalidMsgReceived();
                }
            case 2:
                if (decodedString[0].equals(UCCommand.PLAYER_ID)) {
                    // Player ID received from server
                    playerId = Integer.parseInt(decodedString[1]);
                    callback.onPlayerRegistered();
                }
                break;
            default:
                onInvalidMsgReceived();
        }
    }

    private void onInvalidMsgReceived() {
        throw new RuntimeException(
                "Incorrect message received from server. "
                        + "Are you connecting to a Universal Controller server?"
        );
    }

    private void sendCmd(String cmd) {
        try {
            socket.writeString(cmd, null);
        } catch (IOException ex) {
            callback.onServerDisconnected();
        }
    }

    private void illegalFloatValue() {
        throw new IllegalArgumentException(
                "Float value must be >= -1.0f and <= 1.0f."
        );
    }
}
