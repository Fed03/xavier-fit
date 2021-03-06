package com.fteotini.Xavier.launcher.process.communication;

import com.fteotini.Xavier.launcher.MinionInputStreamHandler;
import com.fteotini.Xavier.launcher.minion.MutationResult;

import java.io.IOException;
import java.util.function.Consumer;

public class ReceiveData implements Consumer<MinionInputStreamHandler> {
    private MutationResult[] result;

    @Override
    public void accept(MinionInputStreamHandler inputStreamHandler) {
        try {
            result = inputStreamHandler.readObject(MutationResult[].class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MutationResult[] receive() {
        return result;
    }
}
