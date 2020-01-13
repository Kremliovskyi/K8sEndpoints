package getservicesinfo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils {

    public static String normalizeDate(DateTime creationTimestamp) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
        return creationTimestamp != null ? dtf.print(creationTimestamp) : "";
    }
}
