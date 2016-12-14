package website.bloop.app;

import android.location.Location;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class LocationModel {
    private static LocationModel instance;

    private PublishSubject<Location> subject = PublishSubject.create();

    public static LocationModel getInstance() {
        if (instance == null) {
            instance = new LocationModel();
        }
        return instance;
    }

    /**
     * Pass a new location to event listeners
     */
    public void setLocation(Location location) {
        subject.onNext(location);
    }

    public Observable<Location> getLocationObservable() {
        return subject;
    }
}
