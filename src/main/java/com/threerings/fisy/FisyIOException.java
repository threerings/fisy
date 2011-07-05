package com.threerings.fisy;

public class FisyIOException extends FisyPathException
{
    public FisyIOException (String message, Exception cause)
    {
        super(message, cause);
    }

    public FisyIOException (String message)
    {
        super(message);
    }
}
