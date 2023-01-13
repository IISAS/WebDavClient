package sk.uisav.icontrol;
import sk.uisav.icontrol.WebDavClient;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client for the stitching process, contains higher-level methods working in the logic of the stitching process
 * and abstracting from WebDAV technicalities.
 *
 * Use:
 * 1. create an instance of sk.uisav.icontrol.WebDavClient
 * 2. create an instance of sk.uisav.icontrol.StitcherClient using the WebDavClient as an argument
 * 3. use the StitcherClient's methods to get/put your data
 */
public class StitcherClient
{
    WebDavClient client;
    String huginFormat;

    /**
     * Creates new Stitcher client instance based on already initialized WebDAV client object
     * @param wdclient already initialized WebDAV client
     * @param huginFormat the format of file naming used by the hugin step to name created panormamas.
     *                    Default value is '/Microstep/hugin/result/panasonic_fullhd_01-090-000-'yyyyMMddHHmm' - panasonic_fullhd_01-090-315-'yyyyMMddHHmm'.jpg'
     *                    The default value is used in this parameter is null. If you provide your own value, it must
     *                    conform to the @see SimpleDateFormat formatting conventions.
     */
    public StitcherClient(WebDavClient wdclient, String huginFormat)
    {
        this.client = wdclient;
        if(huginFormat == null)
            this.huginFormat = "'/Microstep/hugin/result/panasonic_fullhd_01-090-000-'yyyyMMddHHmm' - panasonic_fullhd_01-090-315-'yyyyMMddHHmm'.jpg'";
        else
            this.huginFormat = huginFormat;
    }


    /**
     * Get a hugin-created panorama based on the date/time it represents
     * @param year the year of the panorama photo
     * @param month the month of the panorama photo (1-12)
     * @param day day in month of the panorama photo
     * @param hour the hour of the panorama photo
     * @param minute the minute of the panorama photo
     * @param outDir the output directory (and optionally a name of file) where to store the downloaded panorama
     * @return the size of the downloaded panorama, or -1 if there was an error
     * @throws IOException
     */
    public long getPanorama(int year, int month, int day, int hour, int minute, String outDir) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month-1, day, hour, minute);
        return getPanorama(cal.getTime(), outDir);
    }

    /**
     * Get a hugin-created panorama based on the date/time it represents
     * @param datetime the date/time of the panorama
     * @param outDir the output directory (and optionally a name of file) where to store the downloaded panorama
     * @return the size of the downloaded panorama, or -1 if there was an error
     * @throws IOException
     */
    public long getPanorama(Date datetime, String outDir) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat(this.huginFormat);
        String fname = sdf.format(datetime);
        return this.client.get(fname, outDir);
    }

    /**
     * Downloads stitcher input files for a specified date/time from WebDAV into a specified local folder
     * @param year year of the specificed date/time
     * @param month month of the specificed date/time
     * @param dom day of month of the specificed date/time
     * @param hour hour of the specificed date/time
     * @param min minute of the specificed date/time
     * @param destFolder the destination folder to download the files into
     * @return an array of int containing the number of downloaded files at index 0 and their total size at index 1
     */
    public int[] getStitcherInputs(int year, int month, int dom, int hour, int min, String destFolder) throws IOException
    {
        /*
         * Logika:
         * stiahnu sa subory /Microstep/[year]/[month]/[dom]/[hour][minute/90_FULLHD/panasonic_fullhd*
         */
        int[] retv = new int[2];
        String sourceDir = String.format("/Microstep/%04d/%02d/%02d/%02d%02d/90_FULLHD", year, month, dom, hour, min);
        List<WebDavClient.WebDavResource> files = client.listResources(sourceDir);
        for(WebDavClient.WebDavResource res: files)
        {
            if(!res.isDirectory())
            {
                retv[1] += client.get(res.getName(), destFolder);
                retv[0] += 1;
            }
        }
        return retv;
    }

    /**
     * Uploads the stitching results for a specified date/time from a specified local folder into WebDAV
     * @param year year of the specificed date/time
     * @param month month of the specificed date/time
     * @param dom day of month of the specificed date/time
     * @param hour hour of the specificed date/time
     * @param min minute of the specificed date/time
     * @param srcFolder the source local folder where the stitcher output files are stored
     * @return the number of uploaded files
     */
    public int putStitcherOutputs(int year, int month, int dom, int hour, int min, String srcFolder) throws IOException
    {
        /*
        Logika:
        - folder na WebDAV ma byt /Microstep/[year]/[month]/[dom]/[hour][minute]/stitch/
        - vsetky subory zo srcFolder tam uploadneme
         */
        String dest = String.format("/Microstep/%04d/%02d/%02d/%02d%02d/stitch", year, month, dom, hour, min);
        client.createDirectory(dest);
        int retv = 0;
        Set<String> inFiles = Stream.of(Objects.requireNonNull(new File(srcFolder).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getPath)
                .collect(Collectors.toSet());
        for(String fname: inFiles)
        {
            client.put(fname, dest);
            retv += 1;
        }
        return retv;
    }

}
