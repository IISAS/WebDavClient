package sk.uisav.icontrol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebDavClient 
{
    private String username;
    private String password;
    private String wdroot;
    private Sardine sardine;

    /**
     * Creates instance of a WebDAV client
     * @param username user name to authenticate
     * @param password password
     * @param wdroot the root URL of the WebDAV file system, for example https://dav.example.com/webdav.php
     */
    public WebDavClient(String username, String password, String wdroot)
    {
        this.username = username;
        this.password = password;
        this.wdroot = wdroot;
        //we strip trailing file separator(s) from the WEebDAV root URL
        while((this.wdroot.charAt(this.wdroot.length()-1) == '/'))
            this.wdroot = this.wdroot.substring(0, this.wdroot.length()-1);

        sardine = SardineFactory.begin(username, password);
    }

    /**
     * Creates a full URL using wdroot and the provided path
     * @param path
     * @return
     */
    private String getFullUrl(String path)
    {
        if(path.length() < 1)
        return wdroot;
        String retv = wdroot;
        if(path.charAt(0) != '/')
            retv += '/';
        retv += path;
        return retv;
    }

    /**
     * Downloads a file from WebDAV to a local file.
     * @param remote the name of the remote file inside the WebDAV server we're using
     * @param local the name of the local downloaded file. If the name points to a directory, the name of the remote file will be used.
     * @return 
     * @throws IOException
     */
    public long get(String remote, String local) throws IOException
    {
        long retv = -1;
        File out = new File(local);
        if(out.isDirectory())
        {
            String[] subDirs = remote.split("/");
            out = new File(local, subDirs[subDirs.length - 1]);
        }
        remote = getFullUrl(remote);
        InputStream is = sardine.get(remote);

        retv = java.nio.file.Files.copy(is, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        is.close();
        return retv;

    }

    /**
     * Uploads a local file to WebDAV
     * @param local
     * @param remote
     */
    public void put(String local, String remote)
    {

    }
    
}
