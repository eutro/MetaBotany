package eutros.botaniapp.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionUtils {

    public static Matcher createMatcherWithTimeout(String stringToMatch, Pattern regularExpressionPattern, int timeoutNS) {
        CharSequence charSequence = new TimeoutRegexCharSequence(stringToMatch, timeoutNS, stringToMatch,
                regularExpressionPattern.pattern());
        return regularExpressionPattern.matcher(charSequence);
    }

    private static class TimeoutRegexCharSequence implements CharSequence {

        private final CharSequence inner;

        private final int timeoutNS;

        private final long timeoutTime;

        private final String stringToMatch;

        private final String regularExpression;

        public TimeoutRegexCharSequence(CharSequence inner, int timeoutNS, String stringToMatch, String regularExpression) {
            super();
            this.inner = inner;
            this.timeoutNS = timeoutNS;
            this.stringToMatch = stringToMatch;
            this.regularExpression = regularExpression;
            timeoutTime = System.nanoTime() + timeoutNS;
        }

        public char charAt(int index) {
            if (System.nanoTime() > timeoutTime) {
                throw new RegexTimeout();
            }
            return inner.charAt(index);
        }

        public int length() {
            return inner.length();
        }

        public CharSequence subSequence(int start, int end) {
            return new TimeoutRegexCharSequence(inner.subSequence(start, end), timeoutNS, stringToMatch, regularExpression);
        }

        @NotNull
        @Override
        public String toString() {
            return inner.toString();
        }

    }

    public static class RegexTimeout extends RuntimeException {}
}