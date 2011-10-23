package com.threerings.fisy;

import java.util.Iterator;

/**
 * A path in a fisy file system that contains other paths, either Directory or Record.
 */
public interface Directory
    extends Path, Iterable<Path>
{

    /**
     * Returns an iterator over all the paths in this directory. If the nothing exists here, an
     * empty Iterator will be returned. If something other than a directory exists here or if an
     * error occurs in the underlying IO operations, an OperationException will be raised.
     */
    @Override Iterator<Path> iterator ();

    void move (Directory destination);

    void copy (Directory destination);
}
