package code.session.request;

import code.session.Storage;

public class PutRequest implements Request<Void>{
    private String key;
    private String value;

    public PutRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override public Void handle(Storage storage) {
        storage.put(key, value);

        return null;
    }
}
