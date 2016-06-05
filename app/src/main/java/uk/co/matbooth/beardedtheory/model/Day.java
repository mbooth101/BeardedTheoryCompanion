package uk.co.matbooth.beardedtheory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Day implements Parcelable {

    private Date date;

    public Day() {
    }

    private Day(Parcel in) {
        long time = in.readLong();
        if (time != 0L) {
            date = new Date(time);
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        // It's pretty amazing that these suffixes are not built into the formatter...
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String suffix;
        switch (cal.get(Calendar.DAY_OF_MONTH) % 10) {
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix = "nd";
                break;
            case 3:
                suffix = "rd";
                break;
            default:
                suffix = "th";
                break;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) >= 11 && cal.get(Calendar.DAY_OF_MONTH) <= 13) {
            suffix = "th";
        }
        return String.format(Locale.UK, "%1$ta %1$te", date) + suffix;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day = (Day) o;

        return date != null ? date.equals(day.date) : day.date == null;
    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong((date == null) ? 0L : date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
