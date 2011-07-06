package com.threerings.fisy;

import java.io.IOException;
import org.junit.Test;

public class S3PathMoveTest extends PathMoveTest
{
    @Test
    public void moveLocalToS3 ()
        throws IOException
    {
        testMove(getLocal(), getRemote());
    }

    @Test
    public void moveS3ToSameS3 ()
        throws IOException
    {
        testMove(getRemote(), getRemote());
    }

    @Test
    public void moveS3ToDifferentS3 ()
        throws IOException
    {
        testMove(getRemote(), createRemote(Paths.TEST_S3_URI_FILE));
    }

    @Test
    public void moveS3ToDifferentS3Bucket ()
        throws IOException
    {
        testMove(getRemote(), createRemote("/secondBucket.s3connection"));
    }
}
