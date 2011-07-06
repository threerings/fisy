package com.threerings.fisy.impl.s3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import com.threerings.fisy.TestBase;
import com.threerings.fisy.Record;
import com.threerings.fisy.Path;
import com.threerings.fisy.impl.local.LocalFSTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class S3FSTest extends TestBase
{
    @Test
    public void listMoreThan1000Files ()
        throws IOException, InterruptedException
    {
        final Path path = getRemote();
        ExecutorService ex = Executors.newFixedThreadPool(20);
        for (int ii = 0; ii < 1001; ii++) {
            final int jj = ii;
            ex.execute(new Runnable() {
                @Override
                public void run () {
                    OutputStream out = path.open(String.format("%04d", jj)).write();
                    try {
                        out.write(jj);
                        out.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }});
        }
        ex.shutdown();
        assertTrue(ex.awaitTermination(1, TimeUnit.MINUTES));
        int ii = 0;
        for (Path subpath : getRemote()) {
            assertEquals("The paths should be listed in lexicographical order",
                String.format("%04d", ii++), subpath.getName());
            assertTrue(subpath instanceof Record);
        }
    }

    @Test
    public void deleteNonexistent ()
        throws IOException
    {
        LocalFSTest.deleteNonexistent(getRemote());
    }
}
