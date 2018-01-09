package uk.co.alphaowl.uc;

public interface IUCCallback {
    void onPlayerRegistered();
    void onServerDisconnected();
    void onPlayerNotFound();
    void onServerFull();
    void invalidCmd();
}
