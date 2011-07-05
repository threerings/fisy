package com.threerings.fisy;

public interface FisyPath
{
    /**
     * Returns true if this entry exists in the filesystem. If an error occurs in the underlying
     * IO operations, a FisyIOException will be raised.
     */
    boolean exists();

    /**
     * Deletes this entry and everything contained in it in the filesystem. If the entry doesn't
     * exist, this is a no-op. If the file can't be deleted or if an error occurs in the
     * underlying IO operations, a FisyIOException will be raised.
     */
    void delete();

    /**
     * Returns a reference to a directory in the same fisy file system at the given path. If
     * the path starts with '/', the new directory will be relative to the root of this entry's
     * filesystem. Otherwise, the file is relative to this entry. Each occurrence of '../' at the
     * start of the path moves the returned file one directory above this entry. If the path is
     * relative and this is an instance of FisyFile, the new directory will be relative to the
     * directory the file is contained in. Otherwise, if this is already a directory, the new
     * directory is relative to it.
     */
    FisyDirectory navigate (String path);

    /**
     * Returns a reference to a file in the same fisy file system at the given path. If the
     * path starts with '/', the new file will be relative to the root of this entry's filesystem.
     * Otherwise, the file is relative to this entry. Each occurrence of '../' at the start of the
     * path moves the returned file one directory above this entry. If the path is relative and
     * this is an instance of FisyFile, the new file will be relative to the directory the file is
     * contained in. Otherwise, if this is already a directory, the new file is relative to it.
     */
    FisyFile open(String path);

    /**
     * Returns the path of this entry relative to the root of its filesystem. It always starts
     * with '/'.  If it ends with '/', this is an instance of FisyDirectory. Otherwise, it's an
     * instance of FisyFile.
     */
    String getPath ();

    /**
     * Returns the final segment of this path as returned by {@link #getSegments}.
     */
    String getName ();

    /**
     * Returns the components of this path by splitting on '/' after stripping a trailing '/' if
     * this is a directory.
     */
    String[] getSegments ();
}
