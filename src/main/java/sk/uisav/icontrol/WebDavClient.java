package sk.uisav.icontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebDavClient 
{
    /**
     * Represents a WebDAV resource (a file or a directory)
     */
    public static class WebDavResource
    {
        /**
         * The full name, including the path relative to the WebDAV content root, of a WebDAV resource
         * @return full name, including its path, of a WebDAV resource
         */
        public String getName() {
            return name;
        }

        /**
         * The content type of the WebDAV resource
         * @return the content type of the WebDAV resources
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * The size of the WebDAV resource (-1 for a directory)
         * @return The size of the WebDAV resource (-1 for a directory)
         */
        public long getSize() {
            return size;
        }

        /**
         * Determines whether a WebDAV resource is a directory or not
         * @return true if the resource is a directory, false otherwise
         */
        public boolean isDirectory()
        {
            return this.getContentType() == "httpd/unix-directory";
        }

        private final String name;
        private final String contentType;
        private final long size;
        public WebDavResource(String name, String contentType, long size)
        {
            this.name = name;
            this.contentType = contentType;
            this.size = size;
        }

    }
    private String username;
    private String password;
    private String wdroot;
    private Sardine sardine;
    private String wdpath;

    /**
     * Creates instance of a WebDAV client
     * @param username user name to authenticate
     * @param password password
     * @param wdroot the root URL of the WebDAV file system, for example https://dav.example.com/webdav.php
     */
    public WebDavClient(String username, String password, String wdroot) throws MalformedURLException {
        this.username = username;
        this.password = password;

        this.wdroot = wdroot;
        //we strip trailing file separator(s) from the WEebDAV root URL
        while((this.wdroot.charAt(this.wdroot.length()-1) == '/'))
            this.wdroot = this.wdroot.substring(0, this.wdroot.length()-1);

        URL url = new URL(this.wdroot);
        this.wdpath = url.getPath();

        sardine = SardineFactory.begin(this.username, this.password);
        sardine.enablePreemptiveAuthentication("https://mesos.ui.sav.sk:444");
    }

    /**
     * Creates a full URL using wdroot and the provided path
     * @param path
     * @return
     */
    public String getFullUrl(String path)
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
     * @param local the name of the local file to upload
     * @param remote the name of the remote file to upload
     * @throws IOException
     */
    public void put(String local, String remote) throws IOException
    {
        String fullName = this.getFullUrl(remote);
        sardine.put(fullName, new File(local), "text/plain");
    }

    /**
     * Lists the content of a remote directory 
     * @param remoteDir the remote directory to list
     * @return a list of all the resources (files and directories) in the remote directory
    */
    public List<WebDavResource> listResources(String remoteDir) throws IOException
    {
        List<DavResource> reslist = sardine.list(this.getFullUrl(remoteDir));
        List<WebDavResource> retv = new LinkedList<WebDavResource>();
        for(DavResource res: reslist)
        {
            WebDavResource newRes = new WebDavResource(res.getPath().substring(wdpath.length()), res.getContentType(), res.getContentLength());
            retv.add(newRes);
        }
        return retv;
    }

    /**
     * Return the content of a remote directory as just names
     * @param remoteDir the remote directory to list
     * @return a list of names of resources in the remote directory
     * @throws IOException
     */
    public List<String> list(String remoteDir) throws IOException
    {
        List<WebDavResource> resList = this.listResources(remoteDir);
        List<String> retv = new LinkedList<String>();
        for(WebDavResource res: resList)
            retv.add(res.getName());
        return retv;
    }

    /**
     * Deletes a remote file
     * @param remote the file to delete
     * @return true if the file was successfully deleted, false otherwise
     */
    public void del(String remote) throws IOException {
        sardine.delete(this.getFullUrl(remote));
    }

    /**
     * Finds out whether a remote file exists
     * @param remote the remote file name to check for
     * @return true if file with the given name exists on the remote server, false otherwise
     */
    public boolean exists(String remote) throws IOException {
        return sardine.exists(this.getFullUrl(remote));
    }

    public boolean isDirectory(String remote) throws IOException {
        List<WebDavResource> resList = listResources(remote);
        for(WebDavResource res: resList)
        {
            if(res.getName().replaceAll("/$", "").equals(remote.replaceAll("/$", "")))
                return res.getContentType().equals("httpd/unix-directory");
        }
        return false;

    }

}
