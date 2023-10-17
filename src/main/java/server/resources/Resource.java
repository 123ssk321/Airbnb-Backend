package main.java.server.resources;

import jakarta.ws.rs.WebApplicationException;
import java.util.function.Supplier;

import main.java.utils.Result;

public class Resource {


    protected Resource(){
    }


    protected <T> T getResult(Supplier<Result<T>> resultSupplier){
        var result = resultSupplier.get();
        if(result.isOK())
            return result.value();
        else
            throw new WebApplicationException(result.error());
    }

}