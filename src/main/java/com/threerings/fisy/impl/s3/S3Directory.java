package com.threerings.fisy.impl.s3;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterators;

import com.threerings.fisy.Directory;
import com.threerings.fisy.Path;
import com.threerings.s3.client.S3ObjectEntry;
import com.threerings.s3.client.S3ObjectListing;

public class S3Directory extends S3Path
    implements Directory
{
    public S3Directory (S3Filesystem fs, String path)
    {
        super(fs, path.endsWith("/") ? path : path + "/");
    }

    @Override
    public void delete ()
    {
        for (Path path : this) {
            path.delete();
        }
    }

    @Override
    public Iterator<Path> iterator ()
    {
        return new Iterator<Path>() {
            Iterator<S3ObjectEntry> entries = Iterators.emptyIterator();
            Iterator<String> prefixes = Iterators.emptyIterator();
            String marker;
            boolean hasMoreListings = true;
            Path next;

            @Override public boolean hasNext () {
                if (next != null) {
                    return true;
                } else if (entries.hasNext()) {
                    next = new S3Record(_fs, "/" + entries.next().getKey().substring(_fs.root.length()));
                } else if(prefixes.hasNext()) {
                    next = new S3Directory(_fs, "/" + prefixes.next().substring(_fs.root.length()));
                } else if(hasMoreListings) {
                    S3ObjectListing listing = _fs.list(_path, marker);
                    entries = listing.getEntries().iterator();
                    prefixes = listing.getCommonPrefixes().iterator();
                    marker = listing.getNextMarker();
                    hasMoreListings = listing.truncated();
                    return hasNext();
                }
                return next != null;
            }

            @Override public Path next () {
                if (!hasNext()) {
                    throw new NoSuchElementException("next called without checking hasNext");
                }
                Path current = next;
                next = null;
                return current;
            }

            @Override public void remove () {
                throw new UnsupportedOperationException("Can't remove from s3 while iterating.");
            }
        };
    }

    @Override
    public void move (Directory destination)
    {
        genericMove(this, destination);
    }

    @Override
    public void copy (Directory destination)
    {
        genericCopy(this, destination);
    }
}
