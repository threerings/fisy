package com.threerings.fisy;

import java.util.Iterator;

public class ForwardingFisyDirectory
    implements FisyDirectory
{
    public ForwardingFisyDirectory (FisyDirectory delegate)
    {
        _delegate = delegate;
    }

    public void delete ()
    {
        _delegate.delete();
    }

    public boolean exists ()
    {
        return _delegate.exists();
    }

    public String getName ()
    {
        return _delegate.getName();
    }

    public String getPath ()
    {
        return _delegate.getPath();
    }

    @Override
    public String[] getSegments ()
    {
        return _delegate.getSegments();
    }

    public Iterator<FisyPath> iterator ()
    {
        return _delegate.iterator();
    }

    public FisyDirectory navigate (String path)
    {
        return _delegate.navigate(path);
    }

    public FisyFile open (String path)
    {
        return _delegate.open(path);
    }

    @Override
    public void move (FisyDirectory destination)
    {
        _delegate.move(destination);
    }

    @Override
    public void copy (FisyDirectory destination)
    {
        _delegate.copy(destination);
    }

    protected final FisyDirectory _delegate;
}
