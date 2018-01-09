package uk.co.alphaowl.uc.exceptions;

public class PlayerRegisteredException extends Exception {
    public PlayerRegisteredException() {
        super("A player has already been registered to the server on this "
                + "device.");
    }
}
