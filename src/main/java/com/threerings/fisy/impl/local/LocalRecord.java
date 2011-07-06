package com.threerings.fisy.impl.local;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.threerings.fisy.Record;
import com.threerings.fisy.OperationException;
import com.threerings.fisy.impl.s3.S3Record;

public class LocalRecord extends LocalPath
    implements Record
{
    public LocalRecord (File root, File location, String path)
    {
        super(root, location, path);
    }

    @Override
    public OutputStream overwrite ()
    {
        File parent = _location.getParentFile();
        if (!parent.exists()) {
            // Try to create the directory, and if that fails check if it hasn't been made into a
            // directory in the meantime as another thread may be working here.
            validate(_location.getParentFile().mkdirs() || parent.isDirectory(),
                "Unable to create directory " + parent + " for writing " + this);
        }
        OutputStream os;
        try {
            os = new FileOutputStream(_location);
        } catch (FileNotFoundException e) {
            throw new OperationException("Unable to open " + _location + " for writing", e);
        }
        return new BufferedOutputStream(os);
    }

    @Override
    public boolean exists ()
    {
        return _location.isFile();
    }

    @Override
    public long getModified ()
    {
        validateFileExists();
        return _location.lastModified();
    }

    @Override
    public InputStream read ()
    {
        validateFileExists();
        InputStream is;
        try {
            is = new FileInputStream(_location);
        } catch (FileNotFoundException e) {
            throw new OperationException("Unable to open " + _location + " for reading", e);
        }
        return new BufferedInputStream(is);
    }

    @Override
    public OutputStream write ()
    {
        return write(this);
    }

    @Override
    public long length ()
    {
        validateFileExists();
        validate(!_location.isDirectory(), "Can't get length on a directory", "path", this);
        return _location.length();
    }

    @Override
    public void move (Record destination)
    {
        if (!attemptJavaFileRename(destination)) {
            copy(destination);
            delete();
        }
    }

    @Override
    public void copy (Record destination)
    {
        if (destination instanceof S3Record) {
            try {
                ((S3Record)destination).upload(this);
            } catch (IOException e) {
                throw new OperationException("Unable to move file to s3", e);
            }
        } else {
            genericCopy(this, destination);
        }
    }
}
