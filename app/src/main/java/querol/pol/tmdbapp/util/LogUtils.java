package querol.pol.tmdbapp.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import querol.pol.tmdbapp.BuildConfig;


/**
 * <p>Utility to create logs with the caller's thread and stacktrace on the message</p>
 * <p>Created by Eduardo Ferreras on 08/11/2016.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class LogUtils {
    private static final int SHOW_STACKTRACE = 2;

    public static void v(@NonNull String tag, @NonNull String msg) {
        v(tag, msg, null);
    }

    public static void v(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, getFullMessage(msg), t);
        }
    }


    public static void d(@NonNull String tag, @NonNull String msg) {
        d(tag, msg, null);
    }

    public static void d(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, getFullMessage(msg), t);
        }
    }


    public static void i(@NonNull String tag, @NonNull String msg) {
        i(tag, msg, null);
    }

    public static void i(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        Log.i(tag, getFullMessage(msg), t);
    }


    public static void w(@NonNull String tag, @NonNull String msg) {
        w(tag, msg, null);
    }

    public static void w(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        Log.w(tag, getFullMessage(msg), t);
    }


    public static void e(@NonNull String tag, @NonNull String msg) {
        e(tag, msg, null);
    }

    public static void e(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        Log.e(tag, getFullMessage(msg), t);
    }


    public static void wtf(@NonNull String tag, @NonNull String msg) {
        wtf(tag, msg, null);
    }

    public static void wtf(@NonNull String tag, @NonNull String msg, @Nullable Throwable t) {
        Log.wtf(tag, getFullMessage(msg), t);
    }


    private static String getFullMessage(String msg) {
        return formatMessage(
                getThreadName(),
                getThreadStackTrace(),
                msg
        );
    }

    private static @NonNull String formatMessage(
            @NonNull String threadName,
            @NonNull List<StackTraceElement> stackTrace,
            @NonNull String msg
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("Thread ");
        builder.append("'");
        builder.append(threadName);
        builder.append("'");

        for (int i = 0; i < stackTrace.size(); i += 1) {
            if (i == 0) { builder.append(" at"); }
            StackTraceElement trace = stackTrace.get(i);
            builder.append(" '");
            builder.append(getSimpleClassName(trace));
            builder.append(".");
            builder.append(trace.getMethodName());
            builder.append(" (");
            builder.append(trace.getFileName());
            builder.append(":");
            builder.append(trace.getLineNumber());
            builder.append(")'");
            builder.append(" \n");
            if (i != stackTrace.size() - 1) { builder.append("    in"); }
        }
        if (stackTrace.isEmpty()) { builder.append(" \n"); }

        builder.append("^ ");
        builder.append(msg);
        return builder.toString();
    }

    /**
     * @return The caller thread name
     */
    private static @NonNull String getThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * @param trace A StackTraceElement
     * @return The StackTraceElement's containing class simple name
     */
    private static String getSimpleClassName(StackTraceElement trace) {
        String name = trace.getClassName();
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * @return A List ot the StackTraceElements that go after this class
     */
    private static @NonNull List<StackTraceElement> getThreadStackTrace() {
        List<StackTraceElement> stackTrace = new ArrayList<>();

        boolean found = false;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            if (element.getClassName().equals(LogUtils.class.getName())) {
                found = true;
            } else if (found && stackTrace.size() < SHOW_STACKTRACE) {
                stackTrace.add(element);
            }
        }

        return stackTrace;
    }
}
