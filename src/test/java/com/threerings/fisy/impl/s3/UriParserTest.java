package com.threerings.fisy.impl.s3;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriParserTest
{
    @Test
    public void testNoPath ()
        throws URISyntaxException
    {
        S3Path path = S3Path.from(new URI("s3://id:key@some-bucket"));
        assertEquals("some-bucket", path._fs.bucket);
        assertEquals("", path._fs.root);
    }

    @Test
    public void testEmptyRoot ()
        throws URISyntaxException
    {
        S3Path path = S3Path.from(new URI("s3://id:key@some-bucket/"));
        assertEquals("some-bucket", path._fs.bucket);
        assertEquals("", path._fs.root);
    }

    @Test
    public void testWithRoot ()
        throws URISyntaxException
    {
        S3Path path = S3Path.from(new URI("s3://id:key@adifferentbucket/root_path/blah/"));
        assertEquals("adifferentbucket", path._fs.bucket);
        assertEquals("root_path/blah/", path._fs.root);
    }

    @Test
    public void testEscapedSlashInKey ()
        throws URISyntaxException
    {
        URI uri = new URI("s3://id:abcd%2F1234@some-bucket/");
        S3Path path = S3Path.from(uri);
        assertEquals("some-bucket", path._fs.bucket);
        assertEquals("", path._fs.root);
        String[] idAndKey = S3Path.extractIdAndKey(uri);
        assertEquals("id", idAndKey[0]);
        assertEquals("abcd/1234", idAndKey[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoUserInfo ()
        throws URISyntaxException
    {
        S3Path.from(new URI("s3://some-bucket/"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPartialUserInfo ()
        throws URISyntaxException
    {
        S3Path.from(new URI("s3://id@some-bucket/"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNoBucket ()
        throws URISyntaxException
    {
        S3Path.from(new URI("s3://id:1234@/"));
    }
}
