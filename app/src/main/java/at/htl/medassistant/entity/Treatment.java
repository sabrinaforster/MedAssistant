package at.htl.medassistant.entity;

import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Treatment implements Comparable<Treatment> {

    private static final String TAG = Treatment.class.getSimpleName();

    private Date startDate;
    private Date endDate;
    private Time timeOfTaking; //time per day  HHMM

    private Medicine medicine;
    private User user;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    //region Constructors
    public Treatment(User user, Medicine medicine, Date startDate, Date endDate, Time timeOfTaking) {
        this.user = user;
        this.medicine = medicine;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeOfTaking = timeOfTaking;
    }

    public Treatment(String timeOfTaking) {
        if (!parseTimeOfTaking(timeOfTaking)) {
            Log.e(TAG, "Treatment: parsing of " + timeOfTaking + " failed!");
        }
    }
    //endregion


    //region Getter and Setter
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String hhmm) {
        if (hhmm.matches(TIME24HOURS_PATTERN)) {
            try {
                startDate = dateFormat.parse(hhmm);
            } catch (ParseException e) {
                Log.e(TAG, "setStartDate: " + e.getMessage());;
            }
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getTimeOfTaking() {
        return timeOfTaking;
    }

    public void setTimeOfTaking(Time timeOfTaking) {
        this.timeOfTaking = timeOfTaking;
    }

    // packaged scoped wegen Testen, sonst private
    boolean parseTimeOfTaking(String timeOfTakingString) {
        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
        Matcher matcher = pattern.matcher(timeOfTakingString);
        if (matcher.matches()) {
            try {
                // sql.Time wird von util.Date abgeleitet, daher Verwendung eines Konstruktors
                this.timeOfTaking = new Time(timeFormat.parse(timeOfTakingString).getTime());
                return true;
            } catch (ParseException e) {
                Log.e(TAG, "setTimeOfTaking: " + timeOfTakingString + " --> " + e.getMessage());
            }
        }
        return false;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public String getStartDateToString() {
        String hhmm = dateFormat.format(getStartDate());
        return hhmm;
    }

    public String getEndDateToString() {
        return dateFormat.format(getEndDate());
    }

    public String getTimeOfTakingToString() {
        return new SimpleDateFormat("hh:mm").format(getTimeOfTaking());
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    //endregion


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(user.toString());
        sb.append(": ");
        sb.append(medicine.getName());
        if (startDate != null) {
            sb.append(", vom " + dateFormat.format(startDate));
        }
        if (endDate != null) {
            sb.append(" bis " + dateFormat.format(endDate));
        }
        sb.append(", Time of Taking: " + timeFormat.format(timeOfTaking));
        return sb.toString();
    }


    /*@Override
    public int compare(Treatment treatment2, Treatment treatment1) {
        return treatment1.getTimeOfTaking().compareTo(treatment2.getTimeOfTaking());
    }*/

    @Override
    public int compareTo(Treatment o) {
        int cmp = Long.compare(user.getId(), o.getUser().getId());

        if (cmp == 0) {
            return Long.compare(medicine.getId(), o.getMedicine().getId());
        } else {
            return cmp;
        }
    }

    // @Override
    // public boolean equals(Object o) {
    //    return  this.getTimeOfTakinginTime().equals(((Treatment) o).getTimeOfTakinginTime());
    // }


}
