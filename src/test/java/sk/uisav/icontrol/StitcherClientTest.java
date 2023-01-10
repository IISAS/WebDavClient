package sk.uisav.icontrol;

import junit.framework.TestCase;

import static org.junit.Assert.fail;

public class StitcherClientTest extends TestCase {

    public void testGetPanorama() {
        try {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            StitcherClient sc = new StitcherClient(wdc, null);
            assertEquals(705072, sc.getPanorama(2021, 11, 30, 18, 0,
                    Settings.defaultTestOutDir));
        }
        catch(Exception e)
        {
            fail("Exception: " + e.toString());
        }
    }
}