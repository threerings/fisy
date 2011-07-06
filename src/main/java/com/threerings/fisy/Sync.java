package com.threerings.fisy;

import java.util.concurrent.ExecutorService;
import com.bungleton.yarrgs.Positional;
import com.bungleton.yarrgs.Usage;
import com.bungleton.yarrgs.Yarrgs;

import com.threerings.fisy.util.Executors;

public class Sync
{
    public static class sync
    {
        @Positional(1) @Usage("URI to write to.")
        public String destination;

        @Usage("If ranges existing in the destination should be checked against the source")
        public boolean paranoid;

        @Positional(0) @Usage("URI to read from")
        public String source;
    }

    public static void main (String[] args)
    {
        sync parsed = Yarrgs.parseInMain(sync.class, args);
        FisyDirectory src = FisyPaths.from(parsed.source);
        FisyPath dest = FisyPaths.from(parsed.destination);
        sync(parsed.paranoid, src, dest);
    }

    public static void sync (final boolean paranoid, FisyDirectory src, final FisyPath dest)
    {
        for (final FisyPath item : src) {
            if (item instanceof FisyDirectory) {
                if (!paranoid && item.getName().startsWith("rng")
                    && dest.navigate(item.getPath()).exists()) {
                    continue;
                }
                dir.execute(new Runnable() {
                    @Override public void run () {
                        if (!item.getName().endsWith("meta")) {
                            System.out.println("Syncing " + item);
                        }
                        sync(paranoid, (FisyDirectory)item, dest);
                    }
                });
            } else {
                final FisyFile srcFile = ((FisyFile)item);
                final FisyFile destFile = dest.open(item.getPath());
                file.execute(new Runnable() {
                    @Override public void run () {
                        long destLength = -1;
                        try {
                            destLength = destFile.length();
                        } catch (FisyFileNotFoundException pfnfe) {
                            // Use a not found exception to indicate non-existence rather than
                            // making another possibly remote exists call
                        }
                        if (destLength == srcFile.length()) {
                            return;
                        }
                        try {
                            if (paranoid) {
                                System.out.println("Copying " + srcFile);
                            }
                            srcFile.copy(destFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }});
            }
        }
    }

    public static ExecutorService dir = Executors.newExitingFixedThreadPool("FisypticonDirSync", 5);

    public static ExecutorService file = Executors.newExitingFixedThreadPool("FisypticonFileSync", 10);
}
