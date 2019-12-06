// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import javax.jms.MessageFormatException;
import java.util.List;
import java.util.Calendar;

public class CronParser
{
    private static final int NUMBER_TOKENS = 5;
    private static final int MINUTES = 0;
    private static final int HOURS = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MONTH = 3;
    private static final int DAY_OF_WEEK = 4;
    
    public static long getNextScheduledTime(final String cronEntry, final long currentTime) throws MessageFormatException {
        long result = 0L;
        if (cronEntry == null || cronEntry.length() == 0) {
            return result;
        }
        if (cronEntry.equals("* * * * *")) {
            result = currentTime + 60000L;
            result = result / 60000L * 60000L;
            return result;
        }
        final List<String> list = tokenize(cronEntry);
        final List<CronEntry> entries = buildCronEntries(list);
        final Calendar working = Calendar.getInstance();
        working.setTimeInMillis(currentTime);
        working.set(13, 0);
        final CronEntry minutes = entries.get(0);
        final CronEntry hours = entries.get(1);
        final CronEntry dayOfMonth = entries.get(2);
        final CronEntry month = entries.get(3);
        final CronEntry dayOfWeek = entries.get(4);
        final int timeToNextMinute = 60 - working.get(13);
        working.add(13, timeToNextMinute);
        int currentMinutes = working.get(12);
        if (!isCurrent(minutes, currentMinutes)) {
            final int nextMinutes = getNext(minutes, currentMinutes);
            working.add(12, nextMinutes);
        }
        int currentHours = working.get(11);
        if (!isCurrent(hours, currentHours)) {
            final int nextHour = getNext(hours, currentHours);
            working.add(11, nextHour);
        }
        doUpdateCurrentDay(working, dayOfMonth, dayOfWeek);
        doUpdateCurrentMonth(working, month);
        doUpdateCurrentDay(working, dayOfMonth, dayOfWeek);
        currentHours = working.get(11);
        if (!isCurrent(hours, currentHours)) {
            final int nextHour = getNext(hours, currentHours);
            working.add(11, nextHour);
        }
        currentMinutes = working.get(12);
        if (!isCurrent(minutes, currentMinutes)) {
            final int nextMinutes2 = getNext(minutes, currentMinutes);
            working.add(12, nextMinutes2);
        }
        result = working.getTimeInMillis();
        if (result <= currentTime) {
            throw new ArithmeticException("Unable to compute next scheduled exection time.");
        }
        return result;
    }
    
    protected static long doUpdateCurrentMonth(final Calendar working, final CronEntry month) throws MessageFormatException {
        final int currentMonth = working.get(2) + 1;
        if (!isCurrent(month, currentMonth)) {
            final int nextMonth = getNext(month, currentMonth);
            working.add(2, nextMonth);
            resetToStartOfDay(working, 1);
            return working.getTimeInMillis();
        }
        return 0L;
    }
    
    protected static long doUpdateCurrentDay(final Calendar working, final CronEntry dayOfMonth, final CronEntry dayOfWeek) throws MessageFormatException {
        final int currentDayOfWeek = working.get(7) - 1;
        final int currentDayOfMonth = working.get(5);
        if (!isCurrent(dayOfWeek, currentDayOfWeek) || !isCurrent(dayOfMonth, currentDayOfMonth)) {
            int nextWeekDay = Integer.MAX_VALUE;
            int nextCalendarDay = Integer.MAX_VALUE;
            if (!isCurrent(dayOfWeek, currentDayOfWeek)) {
                nextWeekDay = getNext(dayOfWeek, currentDayOfWeek);
            }
            if (!isCurrent(dayOfMonth, currentDayOfMonth)) {
                nextCalendarDay = getNext(dayOfMonth, currentDayOfMonth);
            }
            if (nextWeekDay < nextCalendarDay) {
                working.add(7, nextWeekDay);
            }
            else {
                working.add(5, nextCalendarDay);
            }
            resetToStartOfDay(working, working.get(5));
            return working.getTimeInMillis();
        }
        return 0L;
    }
    
    public static void validate(final String cronEntry) throws MessageFormatException {
        final List<String> list = tokenize(cronEntry);
        final List<CronEntry> entries = buildCronEntries(list);
        for (final CronEntry e : entries) {
            validate(e);
        }
    }
    
    static void validate(final CronEntry entry) throws MessageFormatException {
        final List<Integer> list = entry.currentWhen;
        if (list.isEmpty() || list.get(0) < entry.start || list.get(list.size() - 1) > entry.end) {
            throw new MessageFormatException("Invalid token: " + entry);
        }
    }
    
    static int getNext(final CronEntry entry, final int current) throws MessageFormatException {
        int result = 0;
        if (entry.currentWhen == null) {
            entry.currentWhen = calculateValues(entry);
        }
        final List<Integer> list = entry.currentWhen;
        int next = -1;
        for (final Integer i : list) {
            if (i > current) {
                next = i;
                break;
            }
        }
        if (next != -1) {
            result = next - current;
        }
        else {
            final int first = list.get(0);
            result = entry.end + first - entry.start - current;
            if (entry.name.equals("DayOfWeek") || entry.name.equals("Month")) {
                ++result;
            }
        }
        return result;
    }
    
    static boolean isCurrent(final CronEntry entry, final int current) throws MessageFormatException {
        final boolean result = entry.currentWhen.contains(new Integer(current));
        return result;
    }
    
    protected static void resetToStartOfDay(final Calendar target, final int day) {
        target.set(5, day);
        target.set(11, 0);
        target.set(12, 0);
        target.set(13, 0);
    }
    
    static List<String> tokenize(final String cron) throws IllegalArgumentException {
        final StringTokenizer tokenize = new StringTokenizer(cron);
        final List<String> result = new ArrayList<String>();
        while (tokenize.hasMoreTokens()) {
            result.add(tokenize.nextToken());
        }
        if (result.size() != 5) {
            throw new IllegalArgumentException("Not a valid cron entry - wrong number of tokens(" + result.size() + "): " + cron);
        }
        return result;
    }
    
    protected static List<Integer> calculateValues(final CronEntry entry) {
        final List<Integer> result = new ArrayList<Integer>();
        if (isAll(entry.token)) {
            for (int i = entry.start; i <= entry.end; ++i) {
                result.add(i);
            }
        }
        else if (isAStep(entry.token)) {
            final int denominator = getDenominator(entry.token);
            final String numerator = getNumerator(entry.token);
            final CronEntry ce = new CronEntry(entry.name, numerator, entry.start, entry.end);
            final List<Integer> list = calculateValues(ce);
            for (final Integer j : list) {
                if (j % denominator == 0) {
                    result.add(j);
                }
            }
        }
        else if (isAList(entry.token)) {
            final StringTokenizer tokenizer = new StringTokenizer(entry.token, ",");
            while (tokenizer.hasMoreTokens()) {
                final String str = tokenizer.nextToken();
                final CronEntry ce = new CronEntry(entry.name, str, entry.start, entry.end);
                final List<Integer> list = calculateValues(ce);
                result.addAll(list);
            }
        }
        else if (isARange(entry.token)) {
            final int index = entry.token.indexOf(45);
            final int first = Integer.parseInt(entry.token.substring(0, index));
            for (int last = Integer.parseInt(entry.token.substring(index + 1)), k = first; k <= last; ++k) {
                result.add(k);
            }
        }
        else {
            final int value = Integer.parseInt(entry.token);
            result.add(value);
        }
        Collections.sort(result);
        return result;
    }
    
    protected static boolean isARange(final String token) {
        return token != null && token.indexOf(45) >= 0;
    }
    
    protected static boolean isAStep(final String token) {
        return token != null && token.indexOf(47) >= 0;
    }
    
    protected static boolean isAList(final String token) {
        return token != null && token.indexOf(44) >= 0;
    }
    
    protected static boolean isAll(final String token) {
        return token != null && token.length() == 1 && (token.charAt(0) == '*' || token.charAt(0) == '?');
    }
    
    protected static int getDenominator(final String token) {
        int result = 0;
        final int index = token.indexOf(47);
        final String str = token.substring(index + 1);
        result = Integer.parseInt(str);
        return result;
    }
    
    protected static String getNumerator(final String token) {
        final int index = token.indexOf(47);
        final String str = token.substring(0, index);
        return str;
    }
    
    static List<CronEntry> buildCronEntries(final List<String> tokens) {
        final List<CronEntry> result = new ArrayList<CronEntry>();
        final CronEntry minutes = new CronEntry("Minutes", tokens.get(0), 0, 60);
        minutes.currentWhen = calculateValues(minutes);
        result.add(minutes);
        final CronEntry hours = new CronEntry("Hours", tokens.get(1), 0, 24);
        hours.currentWhen = calculateValues(hours);
        result.add(hours);
        final CronEntry dayOfMonth = new CronEntry("DayOfMonth", tokens.get(2), 1, 31);
        dayOfMonth.currentWhen = calculateValues(dayOfMonth);
        result.add(dayOfMonth);
        final CronEntry month = new CronEntry("Month", tokens.get(3), 1, 12);
        month.currentWhen = calculateValues(month);
        result.add(month);
        final CronEntry dayOfWeek = new CronEntry("DayOfWeek", tokens.get(4), 0, 6);
        dayOfWeek.currentWhen = calculateValues(dayOfWeek);
        result.add(dayOfWeek);
        return result;
    }
    
    static class CronEntry
    {
        final String name;
        final String token;
        final int start;
        final int end;
        List<Integer> currentWhen;
        
        CronEntry(final String name, final String token, final int start, final int end) {
            this.name = name;
            this.token = token;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return this.name + ":" + this.token;
        }
    }
}
