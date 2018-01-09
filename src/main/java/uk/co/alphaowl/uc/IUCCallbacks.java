package uk.co.alphaowl.uc;

public interface IUCCallbacks {
    void onServerShuttedDown();
    void onPlayerNotFound();
    void invalidCmd();
}
