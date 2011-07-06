package com.threerings.fisy.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;

import com.samskivert.io.StreamUtil;
import com.samskivert.util.Logger;
import com.samskivert.util.StringUtil;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import com.threerings.fisy.RecordExistsException;
import com.threerings.fisy.RecordNotFoundException;
import com.threerings.fisy.OperationException;
import com.threerings.fisy.Path;

/**
 * Implements path name manipulation for a Path, but no underlying filesystem operations.
 */
public abstract class BasePath
    implements Path
{
    public BasePath (String path)
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

    @Override
    public String getName ()
    {
        String[] segs = getSegments();
        return segs[segs.length - 1];
    }

    @Override
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

    protected static OutputStream write (Record file)
    {
        if (file.exists()) {
            throw new RecordExistsException(file + " already exists");
        }
        return file.overwrite();
    }

    protected static void genericMove (Directory src, Directory dest)
    {
        for (Path path : src) {
            if (path instanceof Directory) {
                ((Directory)path).move(dest.navigate(path.getName()));
            } else {
                ((Record)path).move(dest.open(path.getName()));
            }
        }
        src.delete();
    }

    protected static void genericCopy (Directory src, Directory dest)
    {
        for (Path path : src) {
            if (path instanceof Directory) {
                ((Directory)path).copy(dest.navigate(path.getName()));
            } else {
                ((Record)path).copy(dest.open(path.getName()));
            }
        }
    }

    protected static void genericCopy (Record src, Record dest)
    {
        try {
            StreamUtil.copy(src.read(), dest.overwrite()).close();
        } catch (IOException e) {
            throw new OperationException("Unable to move " + dest + " to " + src, e);
        }
    }

    public static void validate (boolean condition, String message, Object... args)
    {
        if (!condition) {
            throw new OperationException(Logger.format(message, args));
        }
    }

    protected void validateFileExists ()
    {
        if (!exists()) {
            throw new RecordNotFoundException("File doesn't exist: " + this);
        }
    }

    protected final String _path;
}
