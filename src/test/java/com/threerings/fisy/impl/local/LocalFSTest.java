package com.threerings.fisy.impl.local;

import org.junit.Test;

import com.threerings.fisy.TestBase;
import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import static org.junit.Assert.assertFalse;

public class LocalFSTest extends TestBase
{
    public static void deleteNonexistent (Directory base)
    {
        Directory subDir = base.navigate("subd");
        assertFalse(subDir.exists());
        subDir.delete();
        assertFalse(subDir.exists());
        Record file = base.open("file");
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
