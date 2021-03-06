package com.fteotini.Xavier.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class MinionInputStreamHandler {
    private final InputStream is;

    public MinionInputStreamHandler(final InputStream is) {
        this.is = is;
    }

    @SuppressWarnings({"SameParameterValue", "unchecked"})
    public <T extends Serializable> T readObject(Class<T> type) throws IOException, ClassNotFoundException {
        try (var objReader = new ObjectInputStream(is)) {
            return (T) objReader.readObject();
        }
    }
}
