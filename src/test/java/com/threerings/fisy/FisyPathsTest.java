package com.threerings.fisy;

import java.io.File;

import org.junit.Test;

import com.threerings.fisy.impl.local.LocalFisyDirectory;
import com.threerings.fisy.impl.s3.S3FisyPath;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

public class FisyPathsTest
{
    @Test
    public void testFileURIParsing ()
    {
        File firstRoot = File.listRoots()[0];
        FisyPath local = FisyPaths.from("file://" + firstRoot.getAbsolutePath());
        assertTrue(local instanceof LocalFisyDirectory);
        assertEquals(firstRoot.getAbsolutePath(), ((LocalFisyDirectory)local).toString());
        local = FisyPaths.from(firstRoot.getAbsolutePath());
        assertEquals(firstRoot.getAbsolutePath(), ((LocalFisyDirectory)local).toString());
    }

    @Test
    public void testS3URIParsing ()
    {
        assertTrue(FisyPaths.from("s3://id:key@some-bucket/") instanceof S3FisyPath);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUnknownSchemeParsing ()
    {
        FisyPaths.from("http://threerings.net");
    }
}
