package ru.gb.perov.client;

import ru.gb.perov.server.AbstractMessage;

public interface OnMessageReceived {

    void OnReceive(AbstractMessage msg);

}
