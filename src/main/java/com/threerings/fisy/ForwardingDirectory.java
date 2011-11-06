package com.threerings.fisy;

import java.util.Iterator;

public class ForwardingDirectory extends ForwardingPath<Directory>
    implements Directory
{
    public ForwardingDirectory (Directory delegate) {
        super(delegate);
    }

    @Override public Iterator<Path> iterator () {
        return _delegate.iterator();
    }

    @Override public void move (Directory destination) {
        _delegate.move(destination);
    }

    @Override public void copy (Directory destination) {
        _delegate.copy(destination);
    }
}
