package sk.uisav.icontrol;
import org.apache.commons.io.FileUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class VisibilityHandlerClientTest extends TestCase {
    @Test
    public void testGetVisibilityHandlerInputsPutOutputs() {
        try {
            FileUtils.cleanDirectory(new File(Settings.defaultTestOutDir));
            WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
            VisibilityHandlerClient vhc = new VisibilityHandlerClient(wdc);
            long x = vhc.getVisibilityHandlerInput(2021, 11, 30, 18, 0, 90, 225, Settings.defaultTestOutDir);
            assertEquals(436213, x);
            int y = vhc.putVisibilityHandlerOutput(2021, 11, 30, 18, 0, 90, 225, Settings.defaultTestOutDir);
            assertEquals(1, y);
        } catch (Exception e) {
            fail("Exception: " + e.toString());
        }
    }
}