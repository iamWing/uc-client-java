package uk.co.alphaowl.uc;

public interface IUCCallbacks {
    void onPlayerRegistered();
    void onServerDisconnected();
    void onPlayerNotFound();
    void onServerFull();
    void invalidCmd();
}
