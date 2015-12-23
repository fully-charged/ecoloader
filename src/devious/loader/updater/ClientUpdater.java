package devious.loader.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClientUpdater {

    public static final String REMOTE_ROOT_PATH = "http://cache.deviousps.com/";
    public static final String REMOTE_CLIENTS_PATH = REMOTE_ROOT_PATH + "clients.txt";

    public static final File CACHE_DIR = new File(System.getProperty("user.home"), "Devious");
    public static final File CLIENTS_DIR = new File(CACHE_DIR, "clients");

    private static final Pattern CLIENT_JAR_PATTERN = Pattern.compile("([\\w\\s\\d]+)-([\\w\\s\\d]+)\\.jar");

    static {
        if(!CACHE_DIR.exists())
            CACHE_DIR.mkdir();
        if(!CLIENTS_DIR.exists())
            CLIENTS_DIR.mkdir();
    }

    public static void download(final ClientVersion cv) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try{
            in = new URL(cv.jarPath).openStream();
            out = new FileOutputStream(new File(CLIENTS_DIR, cv.fileName));
            final byte[] buffer = new byte[4096];
            int len;
            while((len = in.read(buffer)) > 0)
                out.write(buffer, 0, len);
        }finally{
            if(out != null){
                out.flush();
                out.close();
            }
            if(in != null)
                in.close();
        }
    }

    public static List<ClientVersion> loadRemoteVersions() throws IOException {
        final List<ClientVersion> versions = new ArrayList<ClientVersion>();
        Scanner input = null;
        try{
            input = new Scanner(new URL(REMOTE_CLIENTS_PATH).openStream(), "UTF-8");
            while(input.hasNextLine()){
                final String line = input.nextLine().trim();
                if(line.isEmpty())
                    continue;
                final String[] split = line.split("\\s*;\\s*");
                if(split.length != 3)
                    continue;
                versions.add(new ClientVersion(split[0], split[1], REMOTE_ROOT_PATH + split[2]));
            }
            return versions;
        }finally{
            if(input != null)
                input.close();
        }
    }

    public static List<ClientVersion> tryLoadRemoteVersions() {
        try{
            return loadRemoteVersions();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static List<ClientVersion> loadLocalVersions() {
        final List<ClientVersion> versions = new ArrayList<ClientVersion>();
        for(final File file : CLIENTS_DIR.listFiles()){
            final Matcher m = CLIENT_JAR_PATTERN.matcher(file.getName());
            if(!m.matches())
                continue;
            final String name = m.group(1).trim();
            final String version = m.group(2).trim();
            versions.add(new ClientVersion(name, version, file.getPath()));
        }
        return versions;
    }
}
