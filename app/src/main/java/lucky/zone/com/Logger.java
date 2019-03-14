package lucky.zone.com;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by yz on 2018/12/6 8:10 PM
 * Describe: 统一log
 */
public class Logger {

    private static final String PREFIX = "Lucky_";

    /* 调试级别
     * */
    public static void d(String tag, String message) {
            if (TextUtils.isEmpty(message)) {
                message = "**null**";
            }
            int logStrLength = message.length();
            int maxLogSize = 1000;
            for (int i = 0; i <= logStrLength / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > logStrLength ? logStrLength : end;
                Log.d(PREFIX + tag, message.substring(start, end));
            }
    }

    public static void e(String tag, String message) {
        Log.e(PREFIX + tag, message);
    }
    public static void e( String message) {
        Log.e(PREFIX, message);
    }
}
