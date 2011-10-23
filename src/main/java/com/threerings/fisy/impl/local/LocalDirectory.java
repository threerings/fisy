package com.threerings.fisy.impl.local;

import java.util.Iterator;

import java.io.File;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Path;


public class LocalDirectory extends LocalPath
    implements Directory
{
    public LocalDirectory (File root) {
        this(root, root, "/");
    }

    public LocalDirectory (File root, File location, String path) {
        super(root, location, path.endsWith("/") ? path : path + "/");
    }

    @Override public boolean exists () {
        return _location.isDirectory();
    }

    @Override public Iterator<Path> iterator () {
        if (!exists()) {
            return Iterators.emptyIterator();
        }
        validate(_location.isDirectory(), "Not a directory", "path", this);
        return Iterators.transform(Iterators.forArray(_location.listFiles()),
            new Function<File, Path>() {
                @Override public Path apply (File file) {
                    if (file.isDirectory()) {
                        return new LocalDirectory(_root, file, _path + file.getName());
                    } else {
                        return new LocalRecord(_root, file, _path + file.getName());
                    }
                }
            });
    }

    @Override public void delete () {
        for (Path path : this) {
            path.delete();
        }
        super.delete();
    }

    @Override public void move (Directory destination) {
        if (!attemptJavaFileRename(destination)) {
            genericMove(this, destination);
        }
    }

    @Override public void copy (Directory destination) {
        genericCopy(this, destination);
    }
}
