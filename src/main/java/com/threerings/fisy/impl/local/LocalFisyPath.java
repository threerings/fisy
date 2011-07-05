package com.threerings.fisy.impl.local;

import java.io.File;

import com.google.common.base.Preconditions;

import com.samskivert.util.LogBuilder;
import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyFile;
import com.threerings.fisy.FisyIOException;
import com.threerings.fisy.FisyPath;
import com.threerings.fisy.impl.BaseFisyPath;

public abstract class LocalFisyPath extends BaseFisyPath
{
    public LocalFisyPath (File root, File location, String path)
    {
        super(path);
        Preconditions.checkArgument(root != null && root.exists(),
            "LocalFisyDirectory must be given a root that already exists, " + root + " doesn't");
        _root = root;
        _location = location;
    }

    @Override
    public FisyDirectory navigate (String path)
    {
        String normalized = normalize(path);
        return new LocalFisyDirectory(_root, new File(_root, normalized), normalized);
    }

    @Override
    public FisyFile open (String path)
    {
        String normalized = normalize(path);
        return new LocalFisyFile(_root, new File(_root, normalized), normalized);
    }

    @Override
    public void delete ()
    {
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
            throw new FisyIOException(builder.toString());
        }
    }

    protected boolean attemptJavaFileRename (FisyPath destination)
    {
        if (destination instanceof LocalFisyPath) {
            File destLoc = ((LocalFisyPath)destination)._location;
            if (!destLoc.getParentFile().exists()) {
                destLoc.getParentFile().mkdirs();
            }
            return _location.renameTo(((LocalFisyPath)destination)._location);
        }
        return false;
    }

    @Override
    public String toString() {
        return _location.getAbsolutePath();
    }

    protected final File _root, _location;
}
