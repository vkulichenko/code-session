package code.session.request;

import code.session.Storage;

public class PutRequest implements Request<Void> {
    private final String key;

    private final String val;

    public PutRequest(String key, String val) {
        this.key = key;
        this.val = val;
    }

    @Override public Void handle(Storage storage) {
        storage.put(key, val);

        return null;
    }
}
