package dailyreminderapp.Model;

/**
 * Created by Pasa Ibrahim, M Bayu Devara, Solihin.
 *
 * This class represents each history object, an individual incident of a task being taken.
 * Each History object contains the name of the task it is associated with and variables
 * which represent the time and date at which the medication was taken.
 */
public class History {
    private int hourDone;
    private int minuteDone;
    private String dateString;
    private String taskName;

    public int getHourDone() { return hourDone; }

    public void setHourDone(int hourDone) { this.hourDone = hourDone; }

    public int getMinuteDone() { return minuteDone; }

    public void setMinuteDone(int minuteDone) { this.minuteDone = minuteDone; }

    public String getAm_pmDone() { return (hourDone < 12) ? "am" : "pm"; }

    public String getDateString() { return dateString; }

    public void setDateString(String dateString) { this.dateString = dateString; }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
