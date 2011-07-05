package com.threerings.fisy;

public class FisyFileNotFoundException extends FisyPathException
{
    public FisyFileNotFoundException (String message)
    {
        super(message);
    }

    public FisyFileNotFoundException (String message, Exception cause)
    {
        super(message, cause);
    }
}
