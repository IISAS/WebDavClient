package sk.uisav.icontrol;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisibilityHandlerClient
{
    WebDavClient client;
    String huginFormat;

    /**
     * Creates new Stitcher client instance based on already initialized WebDAV client object
     * @param wdclient already initialized WebDAV client
     */
    public VisibilityHandlerClient(WebDavClient wdclient)
    {
        this.client = wdclient;
    }

    /**
     * Downloads visibility handler input file for a specified date/time/pan/azimuth from WebDAV into a specified local folder
     * @param year year of the specificed date/time
     * @param month month of the specificed date/time
     * @param dom day of month of the specificed date/time
     * @param hour hour of the specificed date/time
     * @param min minute of the specificed date/time
     * @param pan the pan of the camera
     * @param azimuth the azimuth of the camera
     * @param destFolder the destination folder to download the files into
     * @return the size of the downloaded file
     */
    public long getVisibilityHandlerInput(int year, int month, int dom, int hour, int min, int pan, int azimuth,
                                            String destFolder) throws IOException
    {
        /*
         * Logika:
         * stiahne sa subor /Microstep/[year]/[month]/[dom]/[hour][minute/90_FULLHD/panasonic_fullhd_01-PAN-AZI-YYYYMMDDHHMM.jpg
         */
        long retv = 0;
        String sourceName = String.format("/Microstep/%04d/%02d/%02d/%02d%02d/90_FULLHD/panasonic_fullhd_01-%03d-%03d-%04d%02d%02d%02d%02d.jpg",
                year, month, dom, hour, min, pan, azimuth, year, month, dom, hour, min);

        retv = client.get(sourceName, destFolder);
        return retv;
    }

    /**
     * Uploads the visibility handler result for a specified date/time from a specified local folder into WebDAV
     * @param year year of the specificed date/time
     * @param month month of the specificed date/time
     * @param dom day of month of the specificed date/time
     * @param hour hour of the specificed date/time
     * @param min minute of the specificed date/time
     * @param srcFolder the source local folder where the stitcher output files are stored
     * @return the number of uploaded files
     */
    public int putVisibilityHandlerOutput(int year, int month, int dom, int hour, int min, int pan, int azimuth, String srcFolder) throws IOException
    {
        /*
        Logika:
        - folder na WebDAV ma byt /Microstep/[year]/[month]/[dom]/[hour][minute]/visibility/pan-azi/
        - vsetky subory zo srcFolder tam uploadneme
         */
        String dest = String.format("/Microstep/%04d/%02d/%02d/%02d%02d/visibility/%03d-%03d",
                year, month, dom, hour, min, pan, azimuth);
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
