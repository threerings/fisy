package com.threerings.fisy.impl.local;

import java.io.File;

import com.google.common.base.Preconditions;

import com.samskivert.util.LogBuilder;
import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import com.threerings.fisy.OperationException;
import com.threerings.fisy.Path;
import com.threerings.fisy.impl.BasePath;

public abstract class LocalPath extends BasePath
{
    public LocalPath (File root, File location, String path) {
        super(path);
        Preconditions.checkArgument(root != null && root.exists(),
            "LocalDirectory must be given a root that already exists, " + root + " doesn't");
        _root = root;
        _location = location;
    }

    public File file () { return _location; }

    @Override public LocalDirectory navigate (String path) {
        String normalized = normalize(path);
        return new LocalDirectory(_root, new File(_root, normalized), normalized);
    }

    @Override public LocalRecord open (String path) {
        String normalized = normalize(path);
        return new LocalRecord(_root, new File(_root, normalized), normalized);
    }

    @Override public void delete () {
        if (exists() && !_location.delete()) {
            LogBuilder builder = new LogBuilder("Unable to delete", "path",
                _location.getAbsolutePath(), "canWrite", _location.canWrite(), "exists", exists());
            if (_location.isFile()) {
                builder.append("type", "file");
            } else if (_location.isDirectory()) {
                builder.append("type", "directory", "listing", _location.list());
            } else {
                builder.append("type", "unknown");
            }
            throw new OperationException(builder.toString());
        }
    }

    protected boolean attemptJavaFileRename (Path destination) {
        if (destination instanceof LocalPath) {
            File destLoc = ((LocalPath)destination)._location;
            if (!destLoc.getParentFile().exists()) {
                destLoc.getParentFile().mkdirs();
            }
            return _location.renameTo(((LocalPath)destination)._location);
        }
        return false;
    }

    @Override public String toString() { return _location.getAbsolutePath(); }

    protected final File _root, _location;
}
