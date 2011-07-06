package com.threerings.fisy;

import java.util.Iterator;

public class ForwardingDirectory
    implements Directory
{
    public ForwardingDirectory (Directory delegate)
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

    public Iterator<Path> iterator ()
    {
        return _delegate.iterator();
    }

    public Directory navigate (String path)
    {
        return _delegate.navigate(path);
    }

    public Record open (String path)
    {
        return _delegate.open(path);
    }

    @Override
    public void move (Directory destination)
    {
        _delegate.move(destination);
    }

    @Override
    public void copy (Directory destination)
    {
        _delegate.copy(destination);
    }

    protected final Directory _delegate;
}
