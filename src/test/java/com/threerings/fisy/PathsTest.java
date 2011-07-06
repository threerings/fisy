package com.threerings.fisy;

import java.io.File;

import org.junit.Test;

import com.threerings.fisy.impl.local.LocalDirectory;
import com.threerings.fisy.impl.s3.S3Path;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

public class PathsTest
{
    @Test
    public void testFileURIParsing ()
    {
        File firstRoot = File.listRoots()[0];
        Path local = Paths.from("file://" + firstRoot.getAbsolutePath());
        assertTrue(local instanceof LocalDirectory);
        assertEquals(firstRoot.getAbsolutePath(), ((LocalDirectory)local).toString());
        local = Paths.from(firstRoot.getAbsolutePath());
        assertEquals(firstRoot.getAbsolutePath(), ((LocalDirectory)local).toString());
    }

    @Test
    public void testS3URIParsing ()
    {
        assertTrue(Paths.from("s3://id:key@some-bucket/") instanceof S3Path);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUnknownSchemeParsing ()
    {
        Paths.from("http://threerings.net");
    }
}
