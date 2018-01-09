package uk.co.alphaowl.uc.exceptions;

public class PlayerNotRegisteredException extends Exception {
    public PlayerNotRegisteredException() {
        super("Player has not been registered to the server yet.");
    }
}
