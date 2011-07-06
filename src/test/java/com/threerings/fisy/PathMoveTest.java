package com.threerings.fisy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.threerings.fisy.TestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathMoveTest extends TestBase
{
    @Test
    public void moveLocalToSameLocal ()
        throws IOException
    {
        testMove(getLocal(), getLocal());
    }

    protected void testMove (Path srcFs, Path destFs)
        throws IOException
    {
        testCopy(srcFs, destFs);
        OutputStream out = srcFs.open("dir/file").write();
        out.write(42);
        out.close();
        srcFs.navigate("dir").move(destFs.navigate("otherdir"));
        assertTrue(destFs.open("otherdir/file").exists());
        assertEquals(42, destFs.open("otherdir/file").read().read());
        assertFalse(srcFs.navigate("dir").exists());
        assertFalse(srcFs.open("dir/file").exists());
    }

    protected void testCopy (Path srcFs, Path destFs)
        throws IOException
    {
        OutputStream out = srcFs.open("cdir/file").write();
        out.write(42);
        out.close();
        srcFs.navigate("cdir").copy(destFs.navigate("cotherdir"));
        assertTrue(destFs.open("cotherdir/file").exists());
        InputStream in = destFs.open("cotherdir/file").read();
        assertEquals(42, in.read());
        assertEquals(-1, in.read());
        assertTrue(srcFs.navigate("cdir").exists());
        assertTrue(srcFs.open("cdir/file").exists());
    }
}
