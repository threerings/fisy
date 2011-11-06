package com.threerings.fisy;

import java.io.InputStream;
import java.io.OutputStream;

public class ForwardingRecord extends ForwardingPath<Record>
    implements Record
{
    public ForwardingRecord (Record delegate) {
        super(delegate);
    }

    @Override public OutputStream write() {
        return _delegate.write();
    }

    @Override public OutputStream overwrite() {
        return _delegate.overwrite();
    }

    @Override public InputStream read () {
        return _delegate.read();
    }

    @Override public long length() {
        return _delegate.length();
    }

    @Override public void move(Record destination) {
        _delegate.move(destination);
    }

    @Override public void copy (Record destination) {
        _delegate.copy(destination);
    }

    @Override public long getModified() {
        return _delegate.getModified();
    }
}
