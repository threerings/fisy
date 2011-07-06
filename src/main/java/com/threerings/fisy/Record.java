package com.threerings.fisy;

import java.io.InputStream;
import java.io.OutputStream;

public interface Record extends Path
{
    /**
     * Returns an OutputStream that writes to this record. If the record already exists, a
     * RecordExistsException will be raised. If an error occurs in the underlying IO
     * operations, an OperationException will be raised.
     */
    OutputStream write();

    /**
     * Returns an OutputStream that writes to this record. If the record already exists, it will be
     * overwritten. If it doesn't exist, a new record will be created. If an error occurs in the
     * underlying IO operations, an OperationException will be raised.
     */
    OutputStream overwrite();

    /**
     * Returns an InputStream that reads from this record. If the record doesn't exist, a
     * RecordNotFoundException will be raised. If an error occurs in the underlying IO
     * operations, an OperationException will be raised.
     */
    InputStream read ();

    /**
     * Returns the number of bytes contained in this record. If the record doesn't exist, an
     * RecordNotFoundException will be raised. If an error occurs in the underlying IO
     * operations, an OperationException will be raised.
     */
    long length ();

    void move (Record destination);

    void copy (Record destination);

    long getModified();
}
