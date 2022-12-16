package sk.uisav.icontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import sk.uisav.icontrol.Settings;

public class WebDavClientTest {
    @Test
    public void testGet() 
    {
        WebDavClient wdc = new WebDavClient(Settings.MesosWDusername, Settings.MesosWDpassword, Settings.MesosWDroot);
        long ret = -1;
        try{
            ret = wdc.get("/Microstep/2020/02/07/2320/90_FULLHD/panasonic_fullhd_01-090-180-202002072320.jpg", "/home/ondrej/Desktop/x.jpg");
        }
        catch(Exception e)
        {
            fail("Exception thrown in get()");
        }
        assertEquals(385830, ret);
    }

    @Test
    public void testPut() {

    }
}
