package at.htl.medassistant.entity;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.htl.medassistant.MedicineDetailsActivity;

public class Treatment implements Comparable<Treatment> {

    private static final String TAG = Treatment.class.getSimpleName();

    private Date startDate;
    private Date endDate;
    private Time timeOfTaking; //time per day  HHMM
    private String note;

    private Medicine medicine;
    private User user;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    public Treatment(Medicine medicine) {

    }

    public Treatment() {
    }

    public Treatment(User user, Medicine medicine, String startDate, String endDate, String timeOfTaking) {
        setStartDate(startDate);
        setEndDate(endDate);
        setTimeOfTaking(timeOfTaking);
        this.user = user;
        //medicine.setPeriodicityInDays(getDaysBetween());
        this.medicine = medicine;

    }

    public Treatment(User user, Medicine medicine, String startDate, String endDate, String timeOfTaking, String note) {
        setStartDate(startDate);
        setEndDate(endDate);
        setTimeOfTaking(timeOfTaking);
        this.user = user;
        this.medicine = medicine;
        this.note = note;
    }

    private int getDaysBetween() {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long different =  endDate.getTime() - startDate.getTime();
        return (int)(different /daysInMilli);
    }

    //region Getter and Setter
    public Date getStartDate() {
        if (startDate == null) {
            return endDate;
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Wandeln Sie den String in ein Datum um
     *
     * @param dateString
     */
    @Deprecated
    public void setStartDate(String dateString) {
        try {
            this.startDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getTimeOfTaking() {
        if (timeOfTaking == null) {
            Calendar now = Calendar.getInstance();
            timeOfTaking = new Time(now.getTimeInMillis());
        }
        return timeOfTaking;
    }

    public void setTimeOfTaking(Time timeOfTaking) {
        this.timeOfTaking = timeOfTaking;
    }

    /**
     * Kontrolliert ob die Uhrzeit korrekt ist (mittels Regex)
     * Anschließend wird der Uhrzeit der Wert zugewiesen
     * this.timeOfTaking = new Time(timeFormat.parse(timeOfTakingString).getTime());
     * <p/>
     * Schlägt das Parsen fehl wird die Uhrzeit nicht verändert
     *
     * @param timeOfTakingString
     * @return
     */
    // packaged scoped wegen Testen, sonst private
    boolean parseTimeOfTaking(String timeOfTakingString) {

        try {
            timeFormat.parse(timeOfTakingString);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }

    public Medicine getMedicine() {
        return medicine;
    }

    public String getStartDateToString() {
        return dateFormat.format(getStartDate());

    }

    public String getEndDateToString() {
        return dateFormat.format(getEndDate());
    }

    public String getTimeOfTakingToString() {
        try {
            return timeFormat.format(getTimeOfTaking());
        } catch (Exception e) {
            return "";
        }
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


    public Treatment(User user, Medicine medicine, Date startDate, Date endDate, Time timeOfTaking) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeOfTaking = timeOfTaking;
        this.medicine = medicine;
        this.user = user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (user != null) {
            sb.append(user.toString());
            sb.append(": ");
        }

        if (medicine != null) {
            sb.append(medicine.getName());
        }

        if (startDate != null) {
            sb.append(", vom " + dateFormat.format(startDate));
        }
        if (endDate != null) {
            sb.append(" bis " + dateFormat.format(endDate));
        }
        if (timeOfTaking != null) {
            sb.append(", Time of Taking: " + timeFormat.format(timeOfTaking));
        }

        return sb.toString();
    }

    @Override
    public int compareTo(Treatment another) {
        return getMedicine().getName().compareTo(another.getMedicine().getName());
    }

    public void setEndDate(String s) {
        try {
            this.endDate = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setTimeOfTaking(String s) {
        try {
            Date date = timeFormat.parse(s);

            Time time = new Time(date.getHours(), date.getMinutes(), date.getMinutes());

            this.timeOfTaking = time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getNote() {
        if (note == null) {
            return "";
        }
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Treatment treatment = (Treatment) o;

        if (startDate != null ? !startDate.equals(treatment.startDate) : treatment.startDate != null)
            return false;
        if (endDate != null ? !endDate.equals(treatment.endDate) : treatment.endDate != null)
            return false;
        if (timeOfTaking != null ? !timeOfTaking.equals(treatment.timeOfTaking) : treatment.timeOfTaking != null)
            return false;
        if (note != null ? !note.equals(treatment.note) : treatment.note != null) return false;
        if (medicine != null ? !medicine.equals(treatment.medicine) : treatment.medicine != null)
            return false;
        if (user != null ? !user.equals(treatment.user) : treatment.user != null) return false;
        if (dateFormat != null ? !dateFormat.equals(treatment.dateFormat) : treatment.dateFormat != null)
            return false;
        return timeFormat != null ? timeFormat.equals(treatment.timeFormat) : treatment.timeFormat == null;

    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (timeOfTaking != null ? timeOfTaking.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (medicine != null ? medicine.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (dateFormat != null ? dateFormat.hashCode() : 0);
        result = 31 * result + (timeFormat != null ? timeFormat.hashCode() : 0);
        return result;
    }
}

