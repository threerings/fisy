package com.threerings.fisy.impl.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.samskivert.util.StringUtil;

import com.threerings.fisy.FisyFile;

public class MemoryFisyFile extends MemoryFisyPath
    implements FisyFile
{
    public MemoryFisyFile (HashMap<String, ByteArrayOutputStream> store, String path)
    {
        super(store, path);
    }

    @Override
    public OutputStream overwrite ()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int idx = _path.lastIndexOf("/", _path.length() - 2);
        String parent = _path.substring(0, idx + 1);
        while (!StringUtil.isBlank(parent) && !_store.containsKey(parent)) {
            // Add a placeholder so we can find the directories later.
            _store.put(parent, new ByteArrayOutputStream());
            idx = parent.lastIndexOf("/", parent.length() - 2);
            parent = parent.substring(0, idx + 1);
        }

        _store.put(_path, out);
        return out;
    }

    @Override
    public InputStream read ()
    {
        validateFileExists();
        return new ByteArrayInputStream(_store.get(_path).toByteArray());
    }

    @Override
    public OutputStream write ()
    {
        return write(this);
    }

    @Override
    public long length ()
    {
        return _store.get(_path).size();
    }

    @Override
    public long getModified ()
    {
        throw new UnsupportedOperationException("MemoryFisyFile doesn't track modified times");
    }

    @Override
    public void move (FisyFile destination)
    {
        copy(destination);
        delete();
    }

    @Override
    public void copy (FisyFile destination)
    {
        genericCopy(this, destination);
    }
}
