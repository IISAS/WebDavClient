package sk.uisav.icontrol;

import com.github.sardine.DavResource;
import org.junit.Test;
import sk.uisav.icontrol.Settings;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class WebDavClientTest {
    @Test
    public void testGet() 
    {
        long ret = -1;
        try{
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            ret = wdc.get("/Microstep/2020/02/07/2320/90_FULLHD/panasonic_fullhd_01-090-180-202002072320.jpg", Settings.defaultTestOutDir);
            String outFileName = Settings.defaultTestOutDir;
            if(! outFileName.endsWith("/"))
                outFileName += "/";
            outFileName += "panasonic_fullhd_01-090-180-202002072320.jpg";
            File f = new File(outFileName);
            f.delete();
        }
        catch(Exception e)
        {
            fail("Exception thrown in get()");
        }
        assertEquals(385830, ret);
    }

    @Test
    public void testPut() {
        try{
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            wdc.put(Settings.defaultTestFile, "/Microstep/test/testfile.txt");
        }
        catch(Exception e)
        {
            fail("Exception thrown in put()");
        }
    }

    @Test
    public void testListResources() {
        try {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            List<WebDavClient.WebDavResource> resList = wdc.listResources("/");
            assertNotNull(resList);
            for(WebDavClient.WebDavResource res: resList)
                System.out.printf("Name: %s Content-type: %s size: %d is-dir: %b\n", res.getName(), res.getContentType(), res.getSize(), res.isDirectory());
        } catch (IOException e) {
            fail("IOException: " + e.toString());
        }
    }

    @Test
    public void testIsDirectory() {
        try
        {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            assertEquals(true, wdc.isDirectory("/"));
            assertEquals(true, wdc.isDirectory("/Microstep"));
            assertEquals(false, wdc.isDirectory("/Nextcloud.png"));

        }
        catch(Exception e)
        {
            fail("Exception: " + e.toString());
        }
    }

    @Test
    public void testCreateDirectory() {
        try
        {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            wdc.createDirectory("/testidr");
        }
        catch(Exception e)
        {
            fail("Exception: " + e.toString());
        }

    }
}
