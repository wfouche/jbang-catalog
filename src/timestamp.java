import static java.lang.System.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class timestamp {

    public static void main(String... args) {
        String dateTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

        out.println(dateTimestamp);
    }
}
