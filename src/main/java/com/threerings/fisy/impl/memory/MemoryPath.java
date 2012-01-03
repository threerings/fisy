package com.threerings.fisy.impl.memory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.google.common.collect.Maps;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import com.threerings.fisy.impl.BasePath;

/**
 * A not terribly efficient in-memory fisy file-system interface, probably only useful for
 *  testing.
 */
public abstract class MemoryPath extends BasePath
{
    public MemoryPath (HashMap<String, ByteArrayOutputStream> store, String path)
    {
        super(path);
        _store = store;
    }

    @Override
    public Directory navigate (String path)
    {
        String normalized = normalize(path);
        return new MemoryDirectory(_store, normalized);
    }

    @Override
    public Record open (String path)
    {
        String normalized = normalize(path);
        return new MemoryRecord(_store, normalized);
    }

    @Override
    public boolean exists ()
    {
        return _store.containsKey(_path);
    }

    @Override
    public void delete ()
    {
        _store.remove(_path);
    }

    /** Creates the root directory for a new fisy filesystem. */
    public static MemoryDirectory createRoot ()
    {
        HashMap<String, ByteArrayOutputStream> output = Maps.newHashMap();
        return new MemoryDirectory(output, "/");
    }

    @Override public String toString() { return "mem:" + _path; }

    protected HashMap<String, ByteArrayOutputStream> _store;
}
