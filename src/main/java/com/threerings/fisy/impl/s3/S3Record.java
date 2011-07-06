package com.threerings.fisy.impl.s3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Supplier;

import com.threerings.fisy.Record;
import com.threerings.fisy.OperationException;
import com.threerings.s3.client.S3ClientException;

public class S3Record extends S3Path
    implements Record
{
    public S3Record (S3Filesystem fs, String path)
    {
        super(fs, path);
    }

    @Override
    public OutputStream overwrite ()
    {
        // Write to a local file first. Since s3 requires Content-Length in the request headers,
        // we need to write everything before sending anything.
        // Might as well calculate an MD5 as we go to make things a little snappier.
        final MessageDigest md = createMD5();

        final File local;
        final OutputStream localOut;
        try {
            local = File.createTempFile("s3fisy", null);
            localOut = new BufferedOutputStream(new FileOutputStream(local));
        } catch (IOException e) {
            throw new OperationException("Unable to create temp file to upload to s3", e);
        }
        return new FilterOutputStream(localOut) {
            @Override public void write (byte[] b) throws IOException {
                md.update(b);
                localOut.write(b);
            }

            @Override public void write (byte[] b, int off, int len) throws IOException {
                md.update(b, off, len);
                localOut.write(b, off, len);
            }

            @Override public void write (int b) throws IOException {
                md.update((byte)b);
                localOut.write(b);
            }

            @Override public void close () throws IOException {
                super.close();
                // Now we've got the full output local, time to start sending it to amazon.
                Supplier<InputStream> supplier = new Supplier<InputStream>() {
                    @Override public InputStream get () {
                        InputStream is;
                        try {
                            is = new FileInputStream(local);
                        } catch (FileNotFoundException e) {
                            throw new OperationException("Unable to open " + local + " for reading", e);
                        }
                        return new BufferedInputStream(is);
                    }};
                _fs.putObject(_path, supplier, local.length(), md.digest());
                // Got it over to them, delete our copy.
                local.delete();
            }
        };
    }

    protected MessageDigest createMD5 ()
    {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void upload (final Record location)
        throws IOException
    {
        MessageDigest md = createMD5();
        InputStream in = location.read();
        byte[] buff = new byte[8192];
        for (int ii = 0; (ii = in.read(buff)) != -1;) {
            md.update(buff, 0, ii);
        }
        Supplier<InputStream> supplier = new Supplier<InputStream>() {
            @Override public InputStream get () {
                return location.read();
            }};
        _fs.putObject(_path,  supplier, location.length(), md.digest());
    }

    @Override
    public InputStream read ()
    {
        try {
            return _fs.getObject(_path).getInputStream();
        } catch (S3ClientException e) {
            // At the time of writing, this only happens if a S3FileObject is unable to open its
            // underlying file.  Reading from s3 doesn't throw exceptions here.
            throw new OperationException("Unable to get file from s3 " + this, e);
        }
    }

    @Override
    public OutputStream write ()
    {
        return write(this);
    }

    @Override
    public void delete ()
    {
        _fs.delete(_path);
    }

    @Override
    public long length ()
    {
        return _fs.getMetadata(_path).length();
    }

    @Override
    public long getModified()
    {
        return _fs.getMetadata(_path).lastModified();
    }

    @Override
    public void move (Record destination)
    {
        copy(destination);
        delete();
    }

    @Override
    public void copy (Record destination)
    {
        if (destination instanceof S3Record) {
            _fs.copyObject(_path, ((S3Record)destination));
        } else {
            genericCopy(this, destination);
        }
    }
}
