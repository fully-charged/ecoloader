package devious.loader.updater;

public class ClientVersion {

    public final String name;
    public final String version;
    public final String jarPath;

    public final String fileName;

    public ClientVersion(final String name, final String version, final String jarPath) {
        this.name = name;
        this.version = version;
        this.jarPath = jarPath;

        fileName = String.format("%s-%s.jar", name, version);
    }

}
