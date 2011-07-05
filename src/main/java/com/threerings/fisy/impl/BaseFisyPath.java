package com.threerings.fisy.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;

import com.samskivert.io.StreamUtil;
import com.samskivert.util.Logger;
import com.samskivert.util.StringUtil;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyFile;
import com.threerings.fisy.FisyFileExistsException;
import com.threerings.fisy.FisyFileNotFoundException;
import com.threerings.fisy.FisyIOException;
import com.threerings.fisy.FisyPath;

/**
 * Implements path name manipulation for a FisyPath, but no underlying filesystem operations.
 */
public abstract class BaseFisyPath
    implements FisyPath
{
    public BaseFisyPath (String path)
    {
        _path = path;
    }
    /**
     * Returns the given path relative to the given base. If <code>path</code> starts with '/',
     * it's returned directly. Otherwise it's appended to base. For each '../' at the start of
     * path, a directory is removed from the end of base before concatenating them.
     */
    public String normalize (String path)
    {
        if (path.startsWith("/")) {
            return path;
        }
        int idx = 0;

        String base = _path.substring(0, _path.lastIndexOf('/'));
        while (path.startsWith("..", idx)) {
            base = base.substring(0, base.lastIndexOf('/'));
            if (path.startsWith("../", idx)) {
                idx += 3;
            } else if (path.length() - idx == 2) {
                // Handle the last segment being .. with no /
                idx += 2;
            }
        }
        Preconditions.checkArgument(!path.substring(idx).contains(".."),
            "Path contained '..' past the initial segments: " + path);
        return base + '/' + path.substring(idx);
    }

    @Override
    public String getPath ()
    {
        return _path;
    }

    public String getName ()
    {
        String[] segs = getSegments();
        return segs[segs.length - 1];
    }

    public String[] getSegments ()
    {
        if (_path.equals("/")) {
            return new String[0];
        }
        int lastIdx = _path.length();
        if (_path.endsWith("/")) {
            lastIdx--;
        }
        return StringUtil.split(_path.substring(1, lastIdx), "/");
    }

    protected static OutputStream write (FisyFile file)
    {
        if (file.exists()) {
            throw new FisyFileExistsException(file + " already exists");
        }
        return file.overwrite();
    }

    protected static void genericMove (FisyDirectory src, FisyDirectory dest)
    {
        for (FisyPath path : src) {
            if (path instanceof FisyDirectory) {
                ((FisyDirectory)path).move(dest.navigate(path.getName()));
            } else {
                ((FisyFile)path).move(dest.open(path.getName()));
            }
        }
        src.delete();
    }

    protected static void genericCopy (FisyDirectory src, FisyDirectory dest)
    {
        for (FisyPath path : src) {
            if (path instanceof FisyDirectory) {
                ((FisyDirectory)path).copy(dest.navigate(path.getName()));
            } else {
                ((FisyFile)path).copy(dest.open(path.getName()));
            }
        }
    }

    protected static void genericCopy (FisyFile src, FisyFile dest)
    {
        try {
            StreamUtil.copy(src.read(), dest.overwrite()).close();
        } catch (IOException e) {
            throw new FisyIOException("Unable to move " + dest + " to " + src, e);
        }
    }

    public static void validate (boolean condition, String message, Object... args)
    {
        if (!condition) {
            throw new FisyIOException(Logger.format(message, args));
        }
    }

    protected void validateFileExists ()
    {
        if (!exists()) {
            throw new FisyFileNotFoundException("File doesn't exist: " + this);
        }
    }

    protected final String _path;
}
