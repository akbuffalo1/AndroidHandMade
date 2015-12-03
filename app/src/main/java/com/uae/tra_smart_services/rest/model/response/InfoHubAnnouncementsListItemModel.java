package com.uae.tra_smart_services.rest.model.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ak-buffalo on 18.08.15.
 */
@Deprecated
public class InfoHubAnnouncementsListItemModel extends InfoHubListItemModel<InfoHubAnnouncementsListItemModel> implements Parcelable {

    public InfoHubAnnouncementsListItemModel(Parcel in) {
    }

    public InfoHubAnnouncementsListItemModel() {
    }

    public static final Parcelable.Creator<InfoHubAnnouncementsListItemModel> CREATOR
            = new Parcelable.Creator<InfoHubAnnouncementsListItemModel>() {
        public InfoHubAnnouncementsListItemModel createFromParcel(Parcel in) {
            return new InfoHubAnnouncementsListItemModel(in);
        }

        public InfoHubAnnouncementsListItemModel[] newArray(int size) {
            return new InfoHubAnnouncementsListItemModel[size];
        }
    };
}
