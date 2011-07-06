package com.threerings.fisy;

public class RecordNotFoundException extends PathException
{
    public RecordNotFoundException (String message)
    {
        super(message);
    }

    public RecordNotFoundException (String message, Exception cause)
    {
        super(message, cause);
    }
}
