package uk.co.matbooth.beardedtheory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable {

    private long id;
    private Day day;
    private Date startTime;
    private Date endTime;
    private Stage stage;
    private Performer performer;

    public Event() {
    }

    public Event(Parcel in) {
        id = in.readLong();
        day = in.readParcelable(Day.class.getClassLoader());
        long time;
        if ((time = in.readLong()) != 0L) {
            startTime = new Date(time);
        }
        if ((time = in.readLong()) != 0L) {
            endTime = new Date(time);
        }
        stage = in.readParcelable(Stage.class.getClassLoader());
        performer = in.readParcelable(Performer.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Performer getPerformer() {
        return performer;
    }

    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    @Override
    public String toString() {
        return getPerformer().toString() + " " + getStage().toString() + " " + getDay().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return id == event.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(day, flags);
        dest.writeLong((startTime == null) ? 0L : startTime.getTime());
        dest.writeLong((endTime == null) ? 0L : endTime.getTime());
        dest.writeParcelable(stage, flags);
        dest.writeParcelable(performer, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

}
