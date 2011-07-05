package com.threerings.fisy.impl.local;

import java.util.Iterator;

import java.io.File;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyPath;


public class LocalFisyDirectory extends LocalFisyPath
    implements FisyDirectory
{
    public LocalFisyDirectory (File root)
    {
        this(root, root, "/");
    }

    public LocalFisyDirectory (File root, File location, String path)
    {
        super(root, location, path.endsWith("/") ? path : path + "/");
    }

    @Override
    public boolean exists ()
    {
        return _location.isDirectory();
    }

    @Override
    public Iterator<FisyPath> iterator ()
    {
        if (!exists()) {
            return Iterators.emptyIterator();
        }
        validate(_location.isDirectory(), "Not a directory", "path", this);
        return Iterators.transform(Iterators.forArray(_location.listFiles()),
            new Function<File, FisyPath>() {
                @Override public FisyPath apply (File file) {
                    if (file.isDirectory()) {
                        return new LocalFisyDirectory(_root, file, _path + file.getName());
                    } else {
                        return new LocalFisyFile(_root, file, _path + file.getName());
                    }
                }
            });
    }

    @Override
    public void delete ()
    {
        for (FisyPath path : this) {
            path.delete();
        }
        super.delete();
    }

    @Override
    public void move (FisyDirectory destination)
    {
        if (!attemptJavaFileRename(destination)) {
            genericMove(this, destination);
        }
    }

    @Override
    public void copy (FisyDirectory destination)
    {
        genericCopy(this, destination);
    }
}
