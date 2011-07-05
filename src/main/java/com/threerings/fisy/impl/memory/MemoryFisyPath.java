package com.threerings.fisy.impl.memory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.google.common.collect.Maps;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyFile;
import com.threerings.fisy.impl.BaseFisyPath;

/**
 * A not terribly efficient in-memory fisy file-system interface, probably only useful for
 *  testing.
 */
public abstract class MemoryFisyPath extends BaseFisyPath
{
    public MemoryFisyPath (HashMap<String, ByteArrayOutputStream> store, String path)
    {
        super(path);
        _store = store;
    }

    @Override
    public FisyDirectory navigate (String path)
    {
        String normalized = normalize(path);
        return new MemoryFisyDirectory(_store, normalized);
    }

    @Override
    public FisyFile open (String path)
    {
        String normalized = normalize(path);
        return new MemoryFisyFile(_store, normalized);
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
    public static MemoryFisyDirectory createRoot ()
    {
        HashMap<String, ByteArrayOutputStream> output = Maps.newHashMap();
        return new MemoryFisyDirectory(output, "/");
    }

    protected HashMap<String, ByteArrayOutputStream> _store;
}
