package org.legion.aegis.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String FULL_STD_FORMAT 				= "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String STD_FORMAT 					= "yyyy-MM-dd HH:mm:ss";
    public static final String TODAY_FORMAT 				= "yyyy-MM-dd";
    public static final String TIME_24H 					= "HH:mm:ss";
    public static final String TIME_12H 					= "hh:mm:ss";
    public static final String TIME_24H_MILLIS 				= "HH:mm:ss:SSS";
    public static final String TIME_12H_MILLIS 				= "hh:mm:ss:SSS";
    public static final String SLASH_TODAY_FORMAT			= "yyyy/MM/dd";
    public static final String MD_FORMAT 					= "MM/dd";
    public static final String UPSIDE_DOWN_TODAY_FORMAT 	= "dd/MM/yyyy";
    public static final long ONE_DAY_MILLIS					= 24 * 60 * 60 * 1000;
    public static final long ONE_HOUR_MILLIS				= 60 * 60 * 1000;

    public static Date parseDate(String dateStr) {
        Date date = null;
        if (!StringUtils.isEmpty(dateStr)) {
            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(FULL_STD_FORMAT);
                date = dateFormatter.parse(dateStr);
            } catch (ParseException e0) {
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(STD_FORMAT);
                    date = dateFormatter.parse(dateStr);
                } catch (ParseException e1) {
                    try {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat(TODAY_FORMAT);
                        date = dateFormatter.parse(dateStr);
                    } catch (ParseException e2) {
                        try {
                            SimpleDateFormat dateFormatter = new SimpleDateFormat(TIME_24H);
                            date = dateFormatter.parse(dateStr);
                        } catch (ParseException e3) {
                            try {
                                SimpleDateFormat dateFormatter = new SimpleDateFormat(TIME_12H);
                                date = dateFormatter.parse(dateStr);
                            } catch (ParseException e4) {
                                try {
                                    SimpleDateFormat dateFormatter = new SimpleDateFormat(SLASH_TODAY_FORMAT);
                                    date = dateFormatter.parse(dateStr);
                                } catch (ParseException e5) {
                                    try {
                                        SimpleDateFormat dateFormatter = new SimpleDateFormat(MD_FORMAT);
                                        date = dateFormatter.parse(dateStr);
                                    } catch (ParseException e6) {
                                        try {
                                            SimpleDateFormat dateFormatter = new SimpleDateFormat(UPSIDE_DOWN_TODAY_FORMAT);
                                            date = dateFormatter.parse(dateStr);
                                        } catch (ParseException e7) {
                                            try {
                                                SimpleDateFormat dateFormatter = new SimpleDateFormat(TIME_24H_MILLIS);
                                                date = dateFormatter.parse(dateStr);
                                            } catch (ParseException e8) {
                                                try {
                                                    SimpleDateFormat dateFormatter = new SimpleDateFormat(TIME_12H_MILLIS);
                                                    date = dateFormatter.parse(dateStr);
                                                } catch (ParseException e9) {
                                                    date = null;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return date;
    }

    public static Date now() {
        return new Date();
    }

    public static Date parseDate(String dateStr, String format) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
            return dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date today() {
        Date date = null;
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(TODAY_FORMAT);
            date = dateFormatter.parse(dateFormatter.format(now()));
        } catch (ParseException e0) {
            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(SLASH_TODAY_FORMAT);
                date = dateFormatter.parse(dateFormatter.format(now()));
            } catch (ParseException e1) {
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(UPSIDE_DOWN_TODAY_FORMAT);
                    date = dateFormatter.parse(dateFormatter.format(now()));
                } catch (ParseException e2) {
                    date = now();
                }
            }
        }
        return date;
    }

    public static Date today(String format) {
        Date date = null;
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
            date = dateFormatter.parse(dateFormatter.format(now()));
        } catch (Exception e) {
            date = today();
        }
        return date;
    }

    public static Date sameTimeTomorrow(Date current) {
        return addHours(current, 24);
    }

    public static Calendar getCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static int getYear(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date addYear(Date date,int year) {
        if (date == null) {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static Date addMonth(Date date,int month) {
        if (date == null) {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date addWeek(Date date,int week) {
        if (date == null) {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.WEEK_OF_YEAR, week);
        return calendar.getTime();
    }

    public static Date addDay(Date date, int day) {
        if (date == null) {
            return null;
        }
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    public static Date addMillis(Date date, long millis) {
        if (date != null) {
            long time = date.getTime();
            return new Date(time + millis);
        }
        return null;
    }

    public static Date addSeconds(Date date, int seconds) {
        if (date != null) {
            long time = date.getTime();
            return new Date(time + seconds * 1000);
        }
        return null;
    }

    public static Date addMinutes(Date date, int minutes) {
        if (date != null) {
            long time = date.getTime();
            return new Date(time + minutes * 60000);
        }
        return null;
    }

    public static Date addHours(Date date, int hours) {
        if (date != null) {
            long time = date.getTime();
            return new Date(time + hours * ONE_HOUR_MILLIS);
        }
        return null;
    }

    public static Timestamp getTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static Date getDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }

    public static Date getDate(long millis) {
        return new Date(millis);
    }

    public static Date getDate(String dateStr, String format) {
        if (StringUtils.isEmpty(format) || StringUtils.isEmpty(dateStr)) {
            return null;
        }
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    public static int getDaysBetween(Date from, Date to) {
        if (from != null && to != null) {
            return (int) (getMillisBetween(from, to) / ONE_DAY_MILLIS);
        }
        return -1;
    }

    public static int getHoursBetween(Date from, Date to) {
        if (from != null && to != null) {
            return (int) (getMillisBetween(from, to) / ONE_HOUR_MILLIS);
        }
        return -1;
    }


    public static long getMillisBetween(Date from, Date to) {
        if (from == null || to == null)
            return -1;
        long fromMillis = from.getTime();
        long toMillis = to.getTime();
        long temp = toMillis - fromMillis;
        long between = temp >= 0 ? temp : -temp;
        return between;
    }

    public static String getDateString(Date date, String format) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getLogDate(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FULL_STD_FORMAT);
        return dateFormat.format(date);
    }

    public static String getStandardDate(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(STD_FORMAT);
        return dateFormat.format(date);
    }

    public static String getWebDate(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(SLASH_TODAY_FORMAT);
        return dateFormat.format(date);
    }

    public static int getAge(Date birthday) {
        if (birthday != null) {
            int birthYear = getYear(birthday);
            int birthMon = getMonth(birthday);
            int birthDay = getDayOfMonth(birthday);
            int currentYear = getYear(now());
            int currentMon = getMonth(now());
            int currentDay = getDayOfMonth(now());

            int passed = currentYear - birthYear;
            if (currentMon < birthMon)
                return passed - 1;
            else if (currentMon > birthMon)
                return passed;
            else {
                if (currentDay < birthDay)
                    return passed - 1;
                else
                    return passed;
            }
        }
        return -1;
    }

    public static boolean isSame(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        }
        return false;
    }

    public static boolean isBetween(Date date, Date from, Date to) {
        if (date != null && from != null && to != null) {
            if (isSame(date, from) || isSame(date, to))
                return true;
            if (date.after(from) && date.before(to))
                return true;
        }
        return false;
    }

    public static boolean isToday(Date date) {
        if (date != null) {
            return isBetween(date, today(), sameTimeTomorrow(today()));
        }
        return false;
    }

    public static String getConstellation(Date date) {
        if (date != null) {
            int month = getMonth(date) + 1;
            int day = getDayOfMonth(date);
            String constellation = null;
            if (month == 1 && day >= 20 || month == 2 && day <= 18) {
                constellation = "AQUA";
            }
            if (month == 2 && day >= 19 || month == 3 && day <= 20) {
                constellation = "PIS";
            }
            if (month == 3 && day >= 21 || month == 4 && day <= 19) {
                constellation = "ARI";
            }
            if (month == 4 && day >= 20 || month == 5 && day <= 20) {
                constellation = "TAU";
            }
            if (month == 5 && day >= 21 || month == 6 && day <= 21) {
                constellation = "GEM";
            }
            if (month == 6 && day >= 22 || month == 7 && day <= 22) {
                constellation = "CAN";
            }
            if (month == 7 && day >= 23 || month == 8 && day <= 22) {
                constellation = "LEO";
            }
            if (month == 8 && day >= 23 || month == 9 && day <= 22) {
                constellation = "VIR";
            }
            if (month == 9 && day >= 23 || month == 10 && day <= 23) {
                constellation = "LIB";
            }
            if (month == 10 && day >= 24 || month == 11 && day <= 22) {
                constellation = "SCO";
            }
            if (month == 11 && day >= 23 || month == 12 && day <= 21) {
                constellation = "SAG";
            }
            if (month == 12 && day >= 22 || month == 1 && day <= 19) {
                constellation = "CAP";
            }
            return constellation;
        }
        return null;
    }

}
