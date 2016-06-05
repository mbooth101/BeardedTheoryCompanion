package uk.co.matbooth.beardedtheory.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stage implements Parcelable {

    private String name;

    public Stage() {
    }

    public Stage(Parcel in) {
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

        Stage stage = (Stage) o;

        return name != null ? name.equals(stage.name) : stage.name == null;
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

    public static final Parcelable.Creator<Stage> CREATOR = new Parcelable.Creator<Stage>() {
        public Stage createFromParcel(Parcel in) {
            return new Stage(in);
        }

        public Stage[] newArray(int size) {
            return new Stage[size];
        }
    };
}
