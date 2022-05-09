package com.weiwan.dsp.common.utils;


import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.exception.DspException;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CheckTool {

    public static void throwParameterErrorException(final String errMsg) {
        throw new IllegalArgumentException(errMsg);
    }

    public static void throwNullPointException(String errMsg) {
        throw new NullPointerException(errMsg);
    }

    public static void throwUnknownParameterException(String errMsg) {
        throw new InvalidParameterException(errMsg);
    }


    // ------------------------------------------------------------------------
    //  Null checks
    // ------------------------------------------------------------------------

    /**
     * Ensures that the given object reference is not null. Upon violation, a {@code
     * NullPointerException} with no message is thrown.
     *
     * @param reference The object reference
     * @return The object reference itself (generically typed).
     * @throws NullPointerException Thrown, if the passed reference was null.
     */
    public static <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that the given object reference is not null. Upon violation, a {@code
     * NullPointerException} with the given message is thrown.
     *
     * @param reference    The object reference
     * @param errorMessage The message for the {@code NullPointerException} that is thrown if the
     *                     check fails.
     * @return The object reference itself (generically typed).
     * @throws NullPointerException Thrown, if the passed reference was null.
     */
    public static <T> T checkNotNull(@Nullable T reference, @Nullable String errorMessage) {
        if (reference == null) {
            throw new DspException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable T reference, @Nullable DspResultStatus resultStatus) {
        if (reference == null) {
            throw new DspConsoleException(resultStatus);
        }
        return reference;
    }

    public static <T> T checkIsNotNull(@Nullable T reference, @Nullable DspResultStatus resultStatus) {
        if (reference != null) {
            throw new DspConsoleException(resultStatus);
        }
        return reference;
    }

    public static void checkState(@Nullable boolean condition, @Nullable DspResultStatus resultStatus) {
        if (condition) {
            throw new DspConsoleException(resultStatus);
        }
    }

    public static <T> T checkIsNull(@Nullable T reference, @Nullable DspResultStatus resultStatus) {
        if (reference == null) {
            throw new DspConsoleException(resultStatus);
        }
        return reference;
    }

    public static <T> T checkIsNull(@Nullable T reference, @Nullable DspResultStatus resultStatus, String msg) {
        if (reference == null) {
            throw new DspConsoleException(resultStatus, msg);
        }
        return reference;
    }
    /**
     * Ensures that the given object reference is not null. Upon violation, a {@code
     * NullPointerException} with the given message is thrown.
     *
     * <p>The error message is constructed from a template and an arguments array, after a similar
     * fashion as {@link String#format(String, Object...)}, but supporting only {@code %s} as a
     * placeholder.
     *
     * @param reference            The object reference
     * @param errorMessageTemplate The message template for the {@code NullPointerException} that is
     *                             thrown if the check fails. The template substitutes its {@code %s} placeholders with the
     *                             error message arguments.
     * @param errorMessageArgs     The arguments for the error message, to be inserted into the message
     *                             template for the {@code %s} placeholders.
     * @return The object reference itself (generically typed).
     * @throws NullPointerException Thrown, if the passed reference was null.
     */
    public static <T> T checkNotNull(
            T reference,
            @Nullable String errorMessageTemplate,
            @Nullable Object... errorMessageArgs) {

        if (reference == null) {
            throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    // ------------------------------------------------------------------------
    //  Boolean Condition Checking (Argument)
    // ------------------------------------------------------------------------

    /**
     * Checks the given boolean condition, and throws an {@code IllegalArgumentException} if the
     * condition is not met (evaluates to {@code false}).
     *
     * @param condition The condition to check
     * @throws IllegalArgumentException Thrown, if the condition is violated.
     */
    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks the given boolean condition, and throws an {@code IllegalArgumentException} if the
     * condition is not met (evaluates to {@code false}). The exception will have the given error
     * message.
     *
     * @param condition    The condition to check
     * @param errorMessage The message for the {@code IllegalArgumentException} that is thrown if
     *                     the check fails.
     * @throws IllegalArgumentException Thrown, if the condition is violated.
     */
    public static void checkArgument(boolean condition, @Nullable Object errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Checks the given boolean condition, and throws an {@code IllegalArgumentException} if the
     * condition is not met (evaluates to {@code false}).
     *
     * @param condition            The condition to check
     * @param errorMessageTemplate The message template for the {@code IllegalArgumentException}
     *                             that is thrown if the check fails. The template substitutes its {@code %s} placeholders
     *                             with the error message arguments.
     * @param errorMessageArgs     The arguments for the error message, to be inserted into the message
     *                             template for the {@code %s} placeholders.
     * @throws IllegalArgumentException Thrown, if the condition is violated.
     */
    public static void checkArgument(
            boolean condition,
            @Nullable String errorMessageTemplate,
            @Nullable Object... errorMessageArgs) {

        if (!condition) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    // ------------------------------------------------------------------------
    //  Boolean Condition Checking (State)
    // ------------------------------------------------------------------------

    /**
     * Checks the given boolean condition, and throws an {@code IllegalStateException} if the
     * condition is not met (evaluates to {@code false}).
     *
     * @param condition The condition to check
     * @throws IllegalStateException Thrown, if the condition is violated.
     */
    public static void checkState(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks the given boolean condition, and throws an {@code IllegalStateException} if the
     * condition is not met (evaluates to {@code false}). The exception will have the given error
     * message.
     *
     * @param condition    The condition to check
     * @param errorMessage The message for the {@code IllegalStateException} that is thrown if the
     *                     check fails.
     * @throws IllegalStateException Thrown, if the condition is violated.
     */
    public static void checkState(boolean condition, @Nullable String errorMessage) {
        if (!condition) {
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Checks the given boolean condition, and throws an {@code IllegalStateException} if the
     * condition is not met (evaluates to {@code false}).
     *
     * @param condition            The condition to check
     * @param errorMessageTemplate The message template for the {@code IllegalStateException} that
     *                             is thrown if the check fails. The template substitutes its {@code %s} placeholders with
     *                             the error message arguments.
     * @param errorMessageArgs     The arguments for the error message, to be inserted into the message
     *                             template for the {@code %s} placeholders.
     * @throws IllegalStateException Thrown, if the condition is violated.
     */
    public static void checkState(
            boolean condition,
            @Nullable String errorMessageTemplate,
            @Nullable Object... errorMessageArgs) {

        if (!condition) {
            throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures that the given index is valid for an array, list or string of the given size.
     *
     * @param index index to check
     * @param size  size of the array, list or string
     * @throws IllegalArgumentException  Thrown, if size is negative.
     * @throws IndexOutOfBoundsException Thrown, if the index negative or greater than or equal to
     *                                   size
     */
    public static void checkElementIndex(int index, int size) {
        checkArgument(size >= 0, "Size was negative.");
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Ensures that the given index is valid for an array, list or string of the given size.
     *
     * @param index        index to check
     * @param size         size of the array, list or string
     * @param errorMessage The message for the {@code IndexOutOfBoundsException} that is thrown if
     *                     the check fails.
     * @throws IllegalArgumentException  Thrown, if size is negative.
     * @throws IndexOutOfBoundsException Thrown, if the index negative or greater than or equal to
     *                                   size
     */
    public static void checkElementIndex(int index, int size, @Nullable String errorMessage) {
        checkArgument(size >= 0, "Size was negative.");
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.valueOf(errorMessage) + " Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Ensures that future has completed normally.
     *
     * @throws IllegalStateException Thrown, if future has not completed or it has completed
     *                               exceptionally.
     */
    public static void checkCompletedNormally(CompletableFuture<?> future) {
        checkState(future.isDone());
        if (future.isCompletedExceptionally()) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    // ------------------------------------------------------------------------
    //  Utilities
    // ------------------------------------------------------------------------

    /**
     * A simplified formatting method. Similar to {@link String#format(String, Object...)}, but with
     * lower overhead (only String parameters, no locale, no format validation).
     *
     * <p>This method is taken quasi verbatim from the Guava Preconditions class.
     */
    private static String format(@Nullable String template, @Nullable Object... args) {
        final int numArgs = args == null ? 0 : args.length;
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * numArgs);
        int templateStart = 0;
        int i = 0;
        while (i < numArgs) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < numArgs) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < numArgs) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }

    // ------------------------------------------------------------------------

    /**
     * Private constructor to prevent instantiation.
     */
    private CheckTool() {
    }


    /**
     * 检查参数是否为null,如果为null,返回默认值
     *
     * @param obj        待检查参数
     * @param defaultVar 默认值
     * @param <T>        参数类型
     * @return
     */
    public static <T> T checkNullOrDefault(T obj, T defaultVar) {
        if (obj != null) {
            return obj;
        }
        return defaultVar;
    }


    /**
     * 检查参数是否为null
     *
     * @param obj 待检查参数
     * @param <T>
     * @return 如果为null, 将会抛出 {@link NullPointerException}
     */
    public static <T> boolean checkIsNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("variable is empty please check");
        }
        return false;
    }

    /**
     * 检查参数是否为null
     *
     * @param obj 待检查参数
     * @param <T>
     * @return 如果为null, 将会抛出 {@link NullPointerException}
     */
    public static <T> boolean checkIsNull(T obj, String msg) {
        if (obj == null) {
            throw new NullPointerException(msg);
        }
        return false;
    }

    /**
     * 检查参数是否为null
     *
     * @param obj 待检查参数
     * @param <T>
     * @return 如果为null, 将会抛出 {@link RuntimeException}
     */
    public static <T> boolean checkNullSupportExecption(T obj) {
        if (obj == null) {
            throw new RuntimeException("variable is empty please check");
        }
        return false;
    }

    public static boolean checkNotNullOrEmpty(Object obj){
        return !checkNullOrEmpty(obj);
    }

    public static boolean checkNullOrEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!checkNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }


    /**
     * 可以闯进来各种不同的对象,参数 都不能为空 为空返回true
     *
     * @param obj
     * @return 为空 true
     */
    public static boolean checkVersIsNull(Object... obj) {
        for (Object o : obj) {
            if (checkNullOrEmpty(o)) {
                return true;
            }
        }
        return false;
    }

    public static void checkConditionFail(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalStateException(msg);
        }
    }
}
