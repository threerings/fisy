package com.threerings.fisy.impl.memory;

import java.util.HashMap;
import java.util.Iterator;

import java.io.ByteArrayOutputStream;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyPath;

public class MemoryFisyDirectory extends MemoryFisyPath
    implements FisyDirectory
{
    public MemoryFisyDirectory (HashMap<String, ByteArrayOutputStream> store, String path)
    {
        super(store, path.endsWith("/") ? path : path + "/");
    }

    @Override
    public Iterator<FisyPath> iterator ()
    {
        if (!exists()) {
            return Iterators.emptyIterator();
        }

        return Iterators.transform(Iterators.filter(_store.keySet().iterator(),
            new Predicate<String>() {
                public boolean apply (String path) {
                    if (!path.startsWith(_path) || path.equals(_path)) {
                        return false;
                    }
                    int nextSlash = path.indexOf("/", _path.length());
                    return (nextSlash == -1 || nextSlash == (path.length() - 1));
                }
            }),
                new Function<String, FisyPath>() {
                    @Override public FisyPath apply (String path) {
                        if (path.endsWith("/")) {
                            return new MemoryFisyDirectory(_store, path);
                        } else {
                            return new MemoryFisyFile(_store, path);
                        }
                    }
                });
    }

    @Override
    public void move (FisyDirectory destination)
    {
        genericMove(this, destination);
    }

    @Override
    public void copy (FisyDirectory destination)
    {
        genericCopy(this, destination);
    }
}
