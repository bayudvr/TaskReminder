package dailyreminderapp.Model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pasa Ibrahim, M Bayu Devara, Solihin.
 * This class represents each task created by the user and contains the medication's name in
 * a string and a list of all of the task's alarm objects. The task's id is used to access the task in the
 * database.
 */
public class Task {
    private String taskName;
    private long taskId;
    private List<Alarm> alarms = new LinkedList<Alarm>();

    public String getTaskName() { return taskName; }

    public void setTaskName(String taskName) { this.taskName = taskName; }

    /**
     *
     * @param alarm
     * allows a new alarm sto be added to a preexisting alarm
     */
    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
        Collections.sort(alarms);
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskID) {
        this.taskId = taskID;
    }
}
