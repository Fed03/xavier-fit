package com.fteotini.amf.launcher.process.communication;

import java.util.function.Consumer;

public class ReceiveData implements Consumer<MinionInputStreamHandler> {
    @Override
    public void accept(MinionInputStreamHandler inputStreamHandler) {
        throw new RuntimeException("Not Implemented");
    }
}
