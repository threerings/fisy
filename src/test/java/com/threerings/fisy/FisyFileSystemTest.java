package com.threerings.fisy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.google.common.collect.Iterables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FisyFileSystemTest extends FsTestBase
{
    @Test
    public void exerciseLocal ()
        throws IOException
    {
        exercise(getLocal());
    }

    @Test
    public void exerciseS3 ()
        throws IOException
    {
        exercise(getRemote());
    }

    public void exercise (FisyPath root)
        throws IOException
    {
        FisyDirectory eventDir = root.navigate("events/eventname/eventid");
        assertFalse("Navigating to a directory shouldn't create it", eventDir.exists());

        FisyFile file = eventDir.open("column");
        assertFalse("Opening a file shouldn't create its directory", eventDir.exists());
        assertFalse("Opening a file shouldn't create it", file.exists());
        writeOneByte(file, file.write(), 27);
        assertTrue("Writing to file should create its directory", eventDir.exists());
        assertEquals("A directory added to a directory should show up in its listing",
            "/events/", Iterables.getOnlyElement(root.navigate("/")).getPath());

        writeOneByte(file, file.overwrite(), 42);
        FisyFile otherColumn = file.open("../otherEventId/column");
        otherColumn.write().close();
        assertTrue(otherColumn.exists());
        otherColumn.delete();
        assertFalse(otherColumn.exists());
        try {
            otherColumn.length();
            assertTrue("Calling length on a non-existent file should throw FisyNotFound", false);
        } catch (FisyFileNotFoundException pnfe) {}
        assertTrue(otherColumn.navigate("../..").exists());
        assertTrue(otherColumn.navigate("/").exists());
        root.delete();
        assertFalse(root + " shouldn't exist", root.exists());
        assertFalse("Deleting a directory should cause files inside it to no longer exist",
            file.exists());
    }

    protected void writeOneByte (FisyFile file, OutputStream out, int value)
        throws IOException
    {
        out.write(value);
        out.close();
        assertEquals("The length of a file should be equal to the number of bytes written to it",
            1, file.length());
        assertTrue("Writing to a file should cause it to exist", file.exists());
        InputStream in = file.read();
        assertEquals(value, in.read());
        assertEquals(-1, in.read());
        in.close();
    }
}
