package com.threerings.fisy.impl.s3;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterators;

import com.threerings.fisy.FisyDirectory;
import com.threerings.fisy.FisyPath;
import com.threerings.s3.client.S3ObjectEntry;
import com.threerings.s3.client.S3ObjectListing;

public class S3FisyDirectory extends S3FisyPath
    implements FisyDirectory
{
    public S3FisyDirectory (S3FisyFilesystem fs, String path)
    {
        super(fs, path.endsWith("/") ? path : path + "/");
    }

    @Override
    public void delete ()
    {
        for (FisyPath path : this) {
            path.delete();
        }
    }

    @Override
    public Iterator<FisyPath> iterator ()
    {
        return new Iterator<FisyPath>() {
            Iterator<S3ObjectEntry> entries = Iterators.emptyIterator();
            Iterator<String> prefixes = Iterators.emptyIterator();
            String marker;
            boolean hasMoreListings = true;
            FisyPath next;

            @Override public boolean hasNext () {
                if (next != null) {
                    return true;
                } else if (entries.hasNext()) {
                    next = new S3FisyFile(_fs, "/" + entries.next().getKey().substring(_fs.root.length()));
                } else if(prefixes.hasNext()) {
                    next = new S3FisyDirectory(_fs, "/" + prefixes.next().substring(_fs.root.length()));
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

            @Override public FisyPath next () {
                if (!hasNext()) {
                    throw new NoSuchElementException("next called without checking hasNext");
                }
                FisyPath current = next;
                next = null;
                return current;
            }

            @Override public void remove () {
                throw new UnsupportedOperationException("Can't remove from s3 while iterating.");
            }
        };
    }

    @Override
    public void move (FisyDirectory destination)
    {
        genericMove(this, destination);
    }

    @Override
    public void copy (FisyDirectory destination)
    {
        genericCopy(this, destination);
    }
}
