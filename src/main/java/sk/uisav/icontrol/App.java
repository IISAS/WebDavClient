package sk.uisav.icontrol;

import java.io.IOException;
import java.util.List;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String username = "marcel.duris";
    private static final String password = "microstepmicrostep";
    private static final String serverUrl = "https://mesos.ui.sav.sk:444/remote.php/webdav/";
    public static void main( String[] args ) throws IOException
    {
        App myApp = new App();
        myApp.testSardine();
    }

    private void testSardine() throws IOException 
    {
        Sardine sardine = SardineFactory.begin(username, password);
        List<DavResource> resources = sardine.list(serverUrl + "/Microstep/");
        for (DavResource res : resources)
        {
            System.out.println(res); // calls the .toString() method.
        }
    }
}
