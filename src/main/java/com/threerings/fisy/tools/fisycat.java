package com.threerings.fisy.tools;

import java.util.Collections;
import java.util.List;

import java.io.IOException;

import com.bungleton.yarrgs.Positional;
import com.bungleton.yarrgs.Unmatched;
import com.bungleton.yarrgs.Usage;
import com.bungleton.yarrgs.Yarrgs;

import com.samskivert.io.StreamUtil;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyFile;
import com.threerings.fisy.FisyPath;
import com.threerings.fisy.FisyPaths;

public class fisycat
{
    @Positional @Usage("The uri to read and print to stdout")
    public String file;

    @Unmatched @Usage("Additional uris to read and print to stdout")
    public List<String> files = Collections.emptyList();

    public static void main (String[] args)
        throws IOException
    {
        fisycat parsed = Yarrgs.parseInMain(fisycat.class, args);
        dump(FisyPaths.from(parsed.file));
        for (String file : parsed.files) {
            dump(FisyPaths.from(file));
        }
    }

    protected static void dump (FisyPath file)
        throws IOException
    {
        if (!file.exists()) {
            if (file instanceof FisyDirectory) {
                System.err.println("'" + file + "' is a directory, not a file");
            } else {
                System.err.println("'" + file + "' doesn't exist");
            }
        } else {
            StreamUtil.copy(((FisyFile)file).read(), System.out);
        }
    }
}
