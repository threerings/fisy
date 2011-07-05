package com.threerings.fisy.impl.local;

import org.junit.Test;

import com.threerings.fisy.FsTestBase;
import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyFile;
import static org.junit.Assert.assertFalse;

public class LocalFSTest extends FsTestBase
{
    public static void deleteNonexistent (FisyDirectory base)
    {
        FisyDirectory subDir = base.navigate("subd");
        assertFalse(subDir.exists());
        subDir.delete();
        assertFalse(subDir.exists());
        FisyFile file = base.open("file");
        assertFalse(file.exists());
        file.delete();
        assertFalse(file.exists());
        base.delete();
    }
    @Test
    public void deleteNonexistent ()
    {
        deleteNonexistent(getLocal());
    }
}
