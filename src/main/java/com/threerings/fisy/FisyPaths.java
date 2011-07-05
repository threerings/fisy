package com.threerings.fisy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.samskivert.io.StreamUtil;
import com.samskivert.util.RandomUtil;
import com.threerings.fisy.impl.local.LocalFisyDirectory;
import com.threerings.fisy.impl.local.LocalFisyPath;
import com.threerings.fisy.impl.s3.S3FisyPath;

public class FisyPaths
{
    /**
     * The location of the default test s3 URI file on the classpath.
     */
    public static final String TEST_S3_URI_FILE = "/test.s3connection";

    /**
     * Creates a FisyPath based on the given URI.<p>
     *
     * If the URI doesn't start with a scheme, or has the file:// scheme, a {@link LocalFisyPath}
     * is created rooted at the location in the URI eg <code>file:///FisypticonStore</code>.<p>
     *
     * If the URI uses the s3 scheme, an s3 path of the form <code>s3://&lt;AWS ID>:&lt;AWS
     * Key>@&lt;Bucket name>[Root path]</code> is expected eg
     * <code>s3://2381ghH:1hvn@fisy-bucket</code>.<p>
     *
     * If the URI is malformed, lacks information needed by the particular path or is using a
     * scheme other than s3 or file, an IllegalArgumentException will be thrown.
     */
    public static FisyDirectory from (String pathUri)
    {
        URI uri;
        try {
            uri = new URI(pathUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed path uri", e);
        }
        if (uri.getScheme() == null) {
            try {
                uri = new URI("file://" + pathUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Malformed path uri", e);
            }
        }

        if (uri.getScheme().equals("file")) {
            return new LocalFisyDirectory(new File(uri));
        } else if(uri.getScheme().equals("s3")) {
            return S3FisyPath.from(uri);
        }
        throw new IllegalArgumentException("Unknown FisyPath URI scheme [scheme="
            + uri.getScheme() + ", uri=" + pathUri + "]");
    }

    /**
     * Makes a local fisy filesystem in a newly created random directory in the temp
     * directory.
     */
    public static FisyDirectory makeTempFs ()
    {
        File rootFile = new File(System.getProperty("java.io.tmpdir"), "fisy"
            + RandomUtil.rand.nextLong());
        rootFile.mkdir();
        return new LocalFisyDirectory(rootFile);
    }

    /**
     * Makes an s3 FisyPath with a random root in with the uri from test.s3connection on the
     * classpath. If test.s3conection can't be found, returns null.
     */
    public static FisyDirectory makeTestS3Fs ()
        throws IOException
    {
        return makeTestS3Fs(TEST_S3_URI_FILE);
    }

    /**
     * Makes an s3 FisyPath with a random root in with the uri from the given file on the
     * classpath. If the file can't be found, returns null.
     */
    public static FisyDirectory makeTestS3Fs (String connectionClasspathLoc)
        throws IOException
    {
        String uri = makeTestS3Uri(connectionClasspathLoc);
        if (uri == null) {
            return null;
        }
        return FisyPaths.from(uri);
    }

    /**
     * Makes an s3 uri with a random root in with the uri from the given file on the
     * classpath. If the file can't be found, returns null.
     */
    public static String makeTestS3Uri (String connectionClasspathLoc)
        throws IOException
    {
        String uri = loadTestS3Uri(connectionClasspathLoc);
        if (uri == null) {
            return null;
        }
        return uri + "/test" + RandomUtil.rand.nextLong();
    }

    public static String loadTestS3Uri (String connectionClasspathLoc)
        throws IOException
    {
        InputStream stream = FisyPaths.class.getResourceAsStream(connectionClasspathLoc);
        if (stream == null) {
            return null;
        }
        try {
            return StreamUtil.toString(stream, "UTF-8").trim();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read '" + connectionClasspathLoc
                + "' as UTF-8", e);
        }
    }
}
