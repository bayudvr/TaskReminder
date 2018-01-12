package dailyreminderapp.ViewController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dailyreminderapp.Model.History;
import dailyreminderapp.Model.Task;
import dailyreminderapp.Model.TaskBox;

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This activity handles the view and controller of the alert page, which contains
 * a dialog fragment AlertAlarm that shows the dialog box to let the user respond to an alarm.
 * This is the "notification" we are using right now. But it only contains a dialog box so it is
 * not a real notification. We can change this to a real notification that has a ringtone or a
 * vibrating function in the future.
 */

public class AlertActivity extends FragmentActivity {

    private AlarmManager alarmManager;
    private PendingIntent operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlertAlarm alert = new AlertAlarm();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getSupportFragmentManager(), "AlertAlarm");
    }

    // Snooze
    public void doNeutralClick(String taskName){
        final int _id = (int) System.currentTimeMillis();
        final long minute = 60000;
        long snoozeLength = 10;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;

        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
        intent.putExtra("task_name", taskName);

        operation = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** Getting a reference to the System Service ALARM_SERVICE */
        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
        Toast.makeText(getBaseContext(), "Alarm for " + taskName + " was snoozed for 10 minutes", Toast.LENGTH_SHORT).show();

        finish();

    }

    // I took it
    public void doPositiveClick(String taskName){
        TaskBox taskBox = new TaskBox();
        Task task = taskBox.getTaskByName(this, taskName);
        History history = new History();

        Calendar takeTime = Calendar.getInstance();
        Date date = takeTime.getTime();
        String dateString = new SimpleDateFormat("MMM d, yyyy").format(date);

        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";

        history.setHourDone(hour);
        history.setMinuteDone(minute);
        history.setDateString(dateString);
        history.setTaskName(taskName);

        taskBox.addToHistory(this, history);

        String stringMinute;
        if (minute < 10)
            stringMinute = "0" + minute;
        else
            stringMinute = "" + minute;

        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;

        Toast.makeText(getBaseContext(),  taskName + " was taken at "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_SHORT).show();

        Intent returnHistory = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHistory);
        finish();
    }

    // I won't take it
    public void doNegativeClick(){
        finish();
    }
}