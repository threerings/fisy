package com.threerings.fisy.tools;

import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.bungleton.yarrgs.Positional;
import com.bungleton.yarrgs.Unmatched;
import com.bungleton.yarrgs.Usage;
import com.bungleton.yarrgs.Yarrgs;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Record;
import com.threerings.fisy.Path;
import com.threerings.fisy.Paths;

public class fisyls
{
    public static void main (String[] args)
    {
        Yarrgs.parseInMain(fisyls.class, args).execute();
    }

    @Usage("Print modtime and size for files")
    public boolean longFormat;

    @Usage("Descend into subdirectories and list their contents")
    public boolean recursive;

    @Positional(0) @Usage("URI to read from.")
    public String filesystem;

    @Unmatched
    public List<String> paths = Lists.newArrayList("/");

    public void execute ()
    {
        Path root = Paths.from(filesystem);
        for (String path : paths) {
            Directory asDir = root.navigate(path);
            if (asDir.exists()) {
                list(asDir);
            } else {
                Record asFile = root.open(path);
                if (asFile.exists()) {
                    print(asFile);
                } else {
                    System.err.println("'" + path + "' doesn't exist in '" + root + "'");
                }
            }
        }
    }

    protected void list (Directory dir)
    {
        if (recursive) {
            System.out.println(dir.getPath() + ":");
        }
        for (Record file : Iterables.filter(dir, Record.class)) {
            print(file);
        }
        if (recursive) {
            System.out.println();
        }
        for (Directory subdir : Iterables.filter(dir, Directory.class)) {
            if (recursive) {
                list(subdir);
            } else {
                print(subdir);
            }
        }
    }

    protected void print (Path path)
    {
        if (longFormat) {
            if (path instanceof Directory) {
                System.out.print(DIR_INDICATOR);
            } else {
                String length = "" + ((Record)path).length();
                StringBuilder builder = pad(LENGTH_WIDTH - length.length());
                builder.append(length).append(' ');
                builder.append(_modifiedFormat.format(new Date(((Record)path).getModified())));
                System.out.print(builder.append(' ').toString());
            }
        }
        System.out.println(path.getName());
    }

    protected static StringBuilder pad(int spaces)
    {
        StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < spaces; ii++) {
            builder.append(' ');
        }
        return builder;
    }

    protected final int LENGTH_WIDTH = 13;
    protected final String MODIFIED_FORMAT = "yyyy-MM-dd hh:mm:ss";
    protected final int MODIFIED_WIDTH = MODIFIED_FORMAT.length();
    protected final String DIR_INDICATOR =
        pad(LENGTH_WIDTH + MODIFIED_WIDTH + 1 - "DIR".length()).append("DIR ").toString();

    protected final DateFormat _modifiedFormat = new SimpleDateFormat();
}
