package biz.enon.tra.uae.entities;

/**
 * Created by mobimaks on 30.09.2015.
 */
public class UserProfile {

    public String firstName;
    public String lastName;
    public String streetAddress;
    public String phoneNumber;

    public String getUserName() {
        return firstName + " " + lastName;
    }

}
