package uk.co.alphaowl.uc;

public interface IUCCallbacks {
    void onServerDisconnected();
    void onPlayerNotFound();
    void onServerFull();
    void invalidCmd();
}
