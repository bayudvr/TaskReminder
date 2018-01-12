package dailyreminderapp.Model;

import android.content.Context;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pasa Ibrahim, M Bayu Devara, Solihin.
 * Is used to retrieve tasks and alarms by other classes.
 * Has access to and can read and write to the database.
 */
public class TaskBox {
    private DbHelper db;
    private static List<Long> tempIds; // Ids of the alarms to be deleted or edited
    private static String tempName; // Ids of the alarms to be deleted or edited

    public List<Long> getTempIds() { return Collections.unmodifiableList(tempIds); }

    public void setTempIds(List<Long> tempIds) { this.tempIds = tempIds; }

    public String getTempName() { return tempName; }

    public void setTempName(String tempName) { this.tempName = tempName; }

    public List<Task> getTasks(Context c) {
        db = new DbHelper(c);
        List<Task> allTasks = db.getAllTasks();
        db.close();
        return allTasks;
    }

    public long addTask(Context c, Task task) {
        db = new DbHelper(c);
        long taskId = db.createTask(task);
        task.setTaskId(taskId);
        db.close();
        return taskId;
    }

    public Task getTaskByName(Context c, String taskName){
        db = new DbHelper(c);
        Task wantedTask = db.getTaskByName(taskName);
        db.close();
        return wantedTask;
    }

    public void addAlarm(Context c, Alarm alarm, Task task){
        db = new DbHelper(c);
        db.createAlarm(alarm, task.getTaskId());
        db.close();
    }

    public List<Alarm> getAlarms(Context c, int dayOfWeek) throws URISyntaxException {
        db = new DbHelper(c);
        List<Alarm> daysAlarms= db.getAlarmsByDay(dayOfWeek);
        db.close();
        Collections.sort(daysAlarms);
        return daysAlarms;
    }

    public List<Alarm> getAlarmByTask (Context c, String taskName) throws URISyntaxException {
        db = new DbHelper(c);
        List<Alarm> tasksAlarms = db.getAllAlarmsByTask(taskName);
        db.close();
        return tasksAlarms;
    }

    public boolean taskExist(Context c, String taskName) {
        db = new DbHelper(c);
        for(Task task: this.getTasks(c)) {
            if(task.getTaskName().equals(taskName))
                return true;
        }
        return false;
    }

    public void deleteTask(Context c, String taskName) throws URISyntaxException {
        db = new DbHelper(c);
        db.deleteTask(taskName);
        db.close();
    }

    public void deleteAlarm(Context c, long alarmId) {
        db = new DbHelper(c);
        db.deleteAlarm(alarmId);
        db.close();
    }

    public void addToHistory(Context c, History h){
        db = new DbHelper(c);
        db.createHistory(h);
        db.close();
    }

    public List<History> getHistory (Context c){
        db = new DbHelper(c);
        List<History> history = db.getHistory();
        db.close();
        return history;
    }

    public Alarm getAlarmById(Context c, long alarm_id) throws URISyntaxException{
        db = new DbHelper(c);
        Alarm alarm = db.getAlarmById(alarm_id);
        db.close();
        return alarm;
    }

    public int getDayOfWeek(Context c, long alarm_id) throws URISyntaxException{
        db = new DbHelper(c);
        int getDayOfWeek = db.getDayOfWeek(alarm_id);
        db.close();
        return getDayOfWeek;
    }
}
