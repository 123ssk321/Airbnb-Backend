package scc.server.resources;

import jakarta.ws.rs.WebApplicationException;
import java.util.function.Supplier;

import scc.storage.Database;
import scc.utils.Result;

public class Resource {
    protected final Database db;

    protected Resource(Database db){
        this.db = db;
    }


    protected <T> T getResult(Supplier<Result<T>> resultSupplier){
        var result = resultSupplier.get();
        if(result.isOK())
            return result.value();
        else
            throw new WebApplicationException(result.error());
    }

}