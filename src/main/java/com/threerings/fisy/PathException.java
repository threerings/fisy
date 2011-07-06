package com.threerings.fisy;

public class PathException extends RuntimeException
{

    public PathException (String message)
    {
        super(message);
    }

    public PathException (String message, Exception cause)
    {
        super(message, cause);
    }
}
