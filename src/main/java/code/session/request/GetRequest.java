package code.session.request;

import code.session.Storage;

public class GetRequest implements Request<String> {
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    @Override public String handle(Storage storage) {
        return storage.get(key);
    }
}
