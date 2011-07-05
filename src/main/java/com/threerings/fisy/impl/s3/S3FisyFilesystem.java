package com.threerings.fisy.impl.s3;

import java.io.InputStream;

import com.google.common.base.Supplier;

import com.threerings.fisy.FisyFileNotFoundException;
import com.threerings.fisy.FisyIOException;
import com.threerings.s3.client.S3Connection;
import com.threerings.s3.client.S3Exception;
import com.threerings.s3.client.S3Metadata;
import com.threerings.s3.client.S3Object;
import com.threerings.s3.client.S3ObjectListing;
import com.threerings.s3.client.S3ServerException.S3Server404Exception;
import com.threerings.s3.client.S3StreamObject;

import static com.threerings.fisy.Log.log;

/**
 * Provides access to a rooted s3 filesystem. All operations taking a path are relative to that
 * root in the given bucket.
 */
public class S3FisyFilesystem
{
    public S3FisyFilesystem (S3Connection connection, String bucket, String root)
    {
        this.conn = connection;
        this.bucket = bucket;
        if (root.startsWith("/")) {
            root = root.substring(1);
        }
        if (root.length() != 0 && !root.endsWith("/")) {
            root = root + "/";
        }
        this.root = root;
    }

    public final S3Connection conn;

    public final String bucket, root;

    protected S3Metadata getMetadata (String path)
    {
        return execute(new S3Fetch<S3Metadata>("Head", path) {
            @Override public S3Metadata fetch () throws S3Exception {
                return conn.getObjectMetadata(bucket, fullPath);
            }});
    }

    protected S3Object getObject (String path)
    {
        return execute(new S3Fetch<S3Object>("Get", path) {
            @Override public S3Object fetch () throws S3Exception {
                return conn.getObject(bucket, fullPath);
            }});
    }

    /**
     * Lists all elements prefixed by the given path starting in the listing at the given marker.
     */
    public S3ObjectListing list (String path, String marker)
    {
        return list(path, marker, 0, "/");
    }

    /**
     * Returns true if the given path exists in the filesystem. If <code>path</code> ends with
     * '/', it's expected to be a directory and this only checks that a prefix exists there. If it
     * doesn't end with '/', there must be an entry at the path.
     */
    public boolean exists (String path)
    {
        // list without a marker or delimiter so we don't roll up prefixes if this is a file and
        // not a directory
        S3ObjectListing listing = list(path, null, 1, null);
        if (listing.getEntries().isEmpty()) {
            return false;
        } else if (!path.endsWith("/")) {
            // It doesn't end with /, so it's a file and it needs to actually exist. If it does
            // exist, it'll be the one returned by list, as it'll be lexicographically first for
            // its exact prefix.
            return listing.getEntries().get(0).getKey().equals(makeFullPath(path));
        } else {
            return true;
        }
    }

    protected S3ObjectListing list (String path, final String marker, final int numEntries,
        final String delimiter)
    {
        return execute(new S3Fetch<S3ObjectListing>("List", path) {
            @Override public S3ObjectListing fetch () throws S3Exception {
                return conn.listObjects(bucket, fullPath, marker, numEntries, delimiter);
            }});
    }

    public void copyObject (String path, final S3FisyFile dest)
    {
        execute(new S3Op("Copy", path) {
            @Override public void execute () throws S3Exception {
                conn.copyObject(fullPath, dest._fs.makeFullPath(dest.getPath()), bucket,
                    dest._fs.bucket);
            }});
    }

    public void putObject (String path, final Supplier<InputStream> in, final long length,
        final byte[] digest)
    {
        execute(new S3Op("Put", path) {
            @Override public void execute () throws S3Exception {
                conn.putObject(bucket, new S3StreamObject(fullPath, length, digest, in.get()));
            }});
    }

    public void delete (String path)
    {
        execute(new S3Op("Delete", path) {
            @Override public void execute () throws S3Exception {
                conn.deleteObject(bucket, fullPath);
            }
        });
    }

    protected <T> T execute (S3Fetch<T> fetch)
    {
        int tries = 0;
        while (true) {
            try {
                log.debug("Running s3 operation", "op", fetch);
                return fetch.fetch();
            } catch (S3Server404Exception nfe) {
                throw new FisyFileNotFoundException("Couldn't find " + fetch.fullPath, nfe);
            } catch(S3Exception exception) {
                if (!exception.isTransient()) {
                    throw new FisyIOException("Non-transient error encountered from s3",
                        exception);
                }
                // Ramp up from 1 second to 5 minutes between retries after subsequent failures
                long delay = Math.min(300, 1 << ++tries) * 1000;
                if (tries % 3 == 0) {
                    log.warning("Hit repeated transient failure from s3, retrying", "operation",
                        fetch, "failures", tries, "retryDelay", delay, exception);
                }
                long current = System.currentTimeMillis();
                long resumeTime = current + delay;
                while (current <= resumeTime) {
                    try {
                        Thread.sleep(resumeTime - current);
                    } catch (InterruptedException e) {}
                    current = System.currentTimeMillis();
                }
            }
        }
    }

    protected String makeFullPath (String path)
    {
        return root + path.substring(1);
    }

    protected abstract class S3Op extends S3Fetch<Void> {
        public S3Op (String operation, String path) {
            super(operation, path);
        }

        @Override public Void fetch() throws S3Exception {
            execute();
            return null;
        }

        protected abstract void execute() throws S3Exception;
    }

    protected abstract class S3Fetch<T> {
        public S3Fetch(String operation, String path) {
            this.operation = operation;
            this.fullPath = makeFullPath(path);
        }

        protected abstract T fetch() throws S3Exception;

        @Override public String toString () {
            return operation + " " + fullPath;
        }

        public final String operation, fullPath;
    }
}
