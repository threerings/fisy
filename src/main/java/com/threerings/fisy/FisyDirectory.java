package com.threerings.fisy;

import java.util.Iterator;

/**
 * An entry in a fisy file system that contains other entries, either FisyDirectories or
 * FisyFiles.
 */
public interface FisyDirectory
    extends FisyPath, Iterable<FisyPath>
{

    /**
     * Returns an iterator over all the entries in this directory. If the nothing exists here, an
     * empty Iterator will be returned. If something other than a directory exists here or if an
     * error occurs in the underlying IO operations, a FisyIOException will be raised.
     */
    @Override
    Iterator<FisyPath> iterator ();

    void move (FisyDirectory destination);

    void copy (FisyDirectory destination);
}
