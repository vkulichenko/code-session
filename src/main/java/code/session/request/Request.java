package code.session.request;

import java.io.Serializable;
import code.session.Storage;

public interface Request<R> extends Serializable {
    R handle(Storage storage);
}
