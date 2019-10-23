package fr.shingle.utils;

import static com.google.common.base.Strings.isNullOrEmpty;
import com.google.common.base.Strings;

public class PathUtils {
    public static String join(String... parts) {
        StringBuilder res = new StringBuilder();

        for (String part : parts) {
            if (isNullOrEmpty(part)) {
                continue;
            }
            res.append("/").append(part);
        }

        return res.toString().replaceAll("/+", "/");
    }
}
