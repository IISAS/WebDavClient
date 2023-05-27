package sk.uisav.icontrol;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

public class StitcherClientTest extends TestCase {

    /*
    public void testGetPanorama() {
        try {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            StitcherClient sc = new StitcherClient(wdc, null);
            assertEquals(705072, sc.getPanorama(2021, 11, 30, 18, 0,
                    Settings.defaultTestOutDir));
        } catch (Exception e) {
            fail("Exception: " + e.toString());
        }
    }
     */

    @Test
    public void testGetStitcherInputsPutOutputs() {
        try {
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            StitcherClient sc = new StitcherClient(wdc, null);
            int[] x = sc.getStitcherInputs(2021, 11, 30, 18, 0, Settings.defaultTestOutDir);
            //assertEquals(8, x[0]);
            //assertEquals(2746426, x[1]);
            int y = sc.putStitcherOutputs(2021, 11, 30, 18, 0, Settings.defaultTestOutDir);
            assertEquals(8, y);
        } catch (Exception e) {
            fail("Exception: " + e.toString());
        }
    }
}