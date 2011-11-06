package com.threerings.fisy;

public class ForwardingPath<T extends Path>
    implements Path
{
    public ForwardingPath (T delegate) {
        _delegate = delegate;
    }

    @Override public void delete () {
        _delegate.delete();
    }

    @Override public boolean exists () {
        return _delegate.exists();
    }

    @Override public String getName () {
        return _delegate.getName();
    }

    @Override public String getPath () {
        return _delegate.getPath();
    }

    @Override public String[] getSegments () {
        return _delegate.getSegments();
    }

    @Override public Directory navigate (String path) {
        return _delegate.navigate(path);
    }

    @Override public Record open (String path) {
        return _delegate.open(path);
    }

    protected final T _delegate;
}
