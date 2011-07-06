package com.threerings.fisy.impl.s3;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.common.base.Preconditions;

import com.samskivert.util.StringUtil;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import com.threerings.fisy.impl.BasePath;
import com.threerings.s3.client.S3Connection;

public abstract class S3Path extends BasePath
{
    public static S3Directory from (URI uri)
    {
        String[] idAndKey = extractIdAndKey(uri);
        String bucket = uri.getHost();
        String root = uri.getPath();
        // Cut the timeout back to 30 seconds for our connection
        S3Connection conn = new S3Connection(idAndKey[0], idAndKey[1], 30 * 1000);
        return new S3Directory(new S3Filesystem(conn, bucket, root), "");
    }

    // id and key extraction is separated from the main uri parsing method to allow it to be tested
    protected static String[] extractIdAndKey (URI uri)
    {
        try {
            uri = uri.parseServerAuthority();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed s3 path uri: " + e.getMessage(), e);
        }
        Preconditions.checkNotNull(uri.getAuthority());
        String[] idAndKey = StringUtil.split(uri.getUserInfo(), ":");
        Preconditions.checkArgument(idAndKey.length == 2);
        return idAndKey;
    }

    public S3Path(S3Filesystem fs, String path) {
        super(path);
        _fs = fs;
    }

    @Override
    public boolean exists ()
    {
        return _fs.exists(_path);
    }

    @Override
    public Directory navigate (String path)
    {
        return new S3Directory(_fs, normalize(path));
    }

    @Override
    public Record open (String path)
    {
        return new S3Record(_fs, normalize(path));
    }

    @Override
    public String toString ()
    {
        return "s3://" + _fs.bucket + "/" + _fs.makeFullPath(_path);
    }

    protected final S3Filesystem _fs;
}
