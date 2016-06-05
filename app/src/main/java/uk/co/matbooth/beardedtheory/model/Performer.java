package uk.co.matbooth.beardedtheory.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Performer implements Parcelable {

    private String name;

    public Performer() {
    }

    public Performer(Parcel in) {
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Performer performer = (Performer) o;

        return name != null ? name.equals(performer.name) : performer.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Performer> CREATOR = new Parcelable.Creator<Performer>() {
        public Performer createFromParcel(Parcel in) {
            return new Performer(in);
        }

        public Performer[] newArray(int size) {
            return new Performer[size];
        }
    };
}
