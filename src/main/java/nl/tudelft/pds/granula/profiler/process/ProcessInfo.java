package nl.tudelft.pds.granula.profiler.process;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessInfo {
    public enum Status {LIVE, DEAD, LOST, UNKNOWN}

    public static String Path2IpAddress(String path) {
        String ip = "unknown";
        try {
            final Pattern pattern = Pattern.compile("@(.+?)/");
            final Matcher matcher = pattern.matcher(path);
            matcher.find();
            ip = matcher.group(1);
        } catch (Exception e) {
            e.printStackTrace();
            ip = "unparsable";
        }
        return ip;
    }
}
