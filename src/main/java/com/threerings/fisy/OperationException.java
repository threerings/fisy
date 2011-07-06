package com.threerings.fisy;

public class OperationException extends PathException
{
    public OperationException (String message, Exception cause)
    {
        super(message, cause);
    }

    public OperationException (String message)
    {
        super(message);
    }
}
