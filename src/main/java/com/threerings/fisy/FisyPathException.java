package com.threerings.fisy;

public class FisyPathException extends RuntimeException
{

    public FisyPathException (String message)
    {
        super(message);
    }

    public FisyPathException (String message, Exception cause)
    {
        super(message, cause);
    }
}
