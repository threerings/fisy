package com.threerings.fisy;

import java.io.InputStream;
import java.io.OutputStream;

public interface FisyFile extends FisyPath
{
    /**
     * Returns an OutputStream that writes to this file. If the file already exists, a
     * FisyFileExistsException will be raised. If an error occurs in the underlying IO
     * operations, a FisyIOException will be raised.
     */
    OutputStream write();

    /**
     * Returns an OutputStream that writes to this file. If the file already exists, it will be
     * overwritten. If it doesn't exist, a new file will be created. If an error occurs in the
     * underlying IO operations, a FisyIOException will be raised.
     */
    OutputStream overwrite();

    /**
     * Returns an InputStream that reads from this file. If the file doesn't already exist, a
     * FisyFileNotFoundException will be raised. If an error occurs in the underlying IO
     * operations, a FisyIOException will be raised.
     */
    InputStream read ();

    /**
     * Returns the number of bytes contained in this file. If the file doesn't already exist, an
     * FisyFileNotFoundException will be raised. If an error occurs in the underlying IO
     * operations, a FisyIOException will be raised.
     */
    long length ();

    void move (FisyFile destination);

    void copy (FisyFile destination);

    long getModified();
}
