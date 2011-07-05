package com.threerings.fisy;

import java.util.List;

import java.io.IOException;

import org.junit.After;
import com.google.common.collect.Lists;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyPath;
import com.threerings.fisy.FisyPaths;

public class FsTestBase
{
    @After
    public void cleanup ()
        throws Exception
    {
        for (FisyPath path : _toDelete) {
            path.delete();
        }
    }

    public FisyDirectory getLocal ()
    {
        if (_local == null) {
            _local = createLocal();
        }
        return _local;
    }

    public FisyDirectory createLocal ()
    {
        FisyDirectory local = FisyPaths.makeTempFs();
        _toDelete.add(local);
        return local;
    }

    public FisyDirectory createRemote (String classpathLoc) throws IOException
    {
        FisyDirectory remote = FisyPaths.makeTestS3Fs(classpathLoc);
        _toDelete.add(remote);
        return remote;

    }

    public FisyDirectory getRemote ()
        throws IOException
    {
        if (_remote == null) {
            _remote = createRemote(FisyPaths.TEST_S3_URI_FILE);
        }
        return _remote;
    }

    protected FisyDirectory _local, _remote;

    protected List<FisyPath> _toDelete = Lists.newArrayList();
}
