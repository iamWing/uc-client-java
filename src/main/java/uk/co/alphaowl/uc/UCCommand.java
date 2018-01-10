package uk.co.alphaowl.uc;

abstract class UCCommand {

    static final String DELIMITER = "<EOC>";
    static final String SEPRARTOR = ":";

    // Replies from server
    static final String PLAYER_ID = "PLAYER_ID";
    static final String PLAYER_NOT_FOUND = "PLAYER_NOT_FOUND";
    static final String SERVER_SHUTDOWN = "SERVER_SHUTDOWN";
    static final String SERVER_FULL = "SERVER_FULL";
    static final String INVALID_CMD = "INVALID_COMMAND";

    // Commands to send
    private static final String REGISTER = "REGISTER" + SEPRARTOR;
    private static final String DEREGISTER = "DEREGISTER" + SEPRARTOR;
    private static final String KEY_DOWN = SEPRARTOR + "KEY_DOWN" + SEPRARTOR;
    private static final String JOYSTICK = SEPRARTOR + "JOYSTICK" + SEPRARTOR;
    private static final String GYRO = SEPRARTOR + "GYRO" + SEPRARTOR;

    /**
     * @return Returns a valid string of register command.
     */
    static String registerCmd(String playerName) {

        return REGISTER + playerName.replace(SEPRARTOR, "") + DELIMITER;
    }

    /**
     * @return Returns a valid string of dereigster command.
     */
    static String deregisterCmd(int playerId) {

        return DEREGISTER + playerId + DELIMITER;
    }

    static String keyDownCmd(int playerId, String[] data) {
        String cmd = playerId + KEY_DOWN;

        for(int i = 0; i < data.length; i++) {
            cmd += data[i];

            if (i != data.length - 1)
                cmd += SEPRARTOR;
        }

        return cmd + DELIMITER;
    }

    /**
     * @return Returns a valid string of joystick command.
     */
    static String joystickCmd(int playerId, float x, float y) {
        
        return playerId + JOYSTICK + x + SEPRARTOR + y + DELIMITER;
    }

    /**
     * @return Returns a valid string of gyro command.
     */
    static String gyroCmd(int playerId, float x, float y, float z) {

        return playerId + GYRO + x + SEPRARTOR + y + SEPRARTOR + z + DELIMITER;
    }
}