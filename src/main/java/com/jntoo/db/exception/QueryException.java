package com.jntoo.db.exception;

public class QueryException extends RuntimeException {

    private Exception exception;

    public QueryException(){

    }

    public QueryException(String message)
    {
        super(message);
    }

    public QueryException(String message , Exception e)
    {
        super(message,e);
        exception = e;
    }

    public Exception getException() {
        return exception;
    }

}
