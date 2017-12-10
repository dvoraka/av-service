package dvoraka.avservice.client;

import dvoraka.avservice.common.listener.MessageListener;

public interface MessageReceiver<L extends MessageListener> {

    void addMessageListener(L listener);

    void removeMessageListener(L listener);

    int getListenerCount();
}
