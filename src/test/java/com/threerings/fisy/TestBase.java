package com.threerings.fisy;

import java.util.List;

import java.io.IOException;

import org.junit.After;
import com.google.common.collect.Lists;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Path;
import com.threerings.fisy.Paths;

public class TestBase
{
    @After
    public void cleanup ()
        throws Exception
    {
        for (Path path : _toDelete) {
            path.delete();
        }
    }

    public Directory getLocal ()
    {
        if (_local == null) {
            _local = createLocal();
        }
        return _local;
    }

    public Directory createLocal ()
    {
        Directory local = Paths.makeTempFs();
        _toDelete.add(local);
        return local;
    }

    public Directory createRemote (String classpathLoc) throws IOException
    {
        Directory remote = Paths.makeTestS3Fs(classpathLoc);
        _toDelete.add(remote);
        return remote;

    }

    public Directory getRemote ()
        throws IOException
    {
        if (_remote == null) {
            _remote = createRemote(Paths.TEST_S3_URI_FILE);
        }
        return _remote;
    }

    protected Directory _local, _remote;

    protected List<Path> _toDelete = Lists.newArrayList();
}
