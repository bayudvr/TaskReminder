package dailyreminderapp.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SQL database helper class, adapted from DrBFraser code on YouTube.
 * Found at https://youtu.be/Aui-kFuXFYE
 * Code at http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 *
 * This class is used to store data to local memory on the phone.
 * It can be accessed only through the taskBox model class.
 * It can access the task, alarm, and history model classes.
 *
 */

public class DbHelper extends SQLiteOpenHelper {

    /** Database name */
    private static final String DATABASE_NAME = "task_model_database"; //===========================

    /** Database version */
    private static final int DATABASE_VERSION = 3; //===========================

    /** Table names */
    private static final String TASK_TABLE          = "task"; //===========================
    private static final String ALARM_TABLE         = "alarms";
    private static final String TASK_ALARM_LINKS    = "task_alarm"; //===========================
    private static final String HISTORIES_TABLE     = "histories";

    /** Common column name and location */
    public static final String KEY_ROWID            = "id";

    /** Task table columns, used by History Table */
    private static final String KEY_TASKNAME        = "taskName"; //===========================

    /** Alarm table columns, Hour & Minute used by History Table */
    private static final String KEY_INTENT           = "intent";
    private static final String KEY_HOUR             = "hour";
    private static final String KEY_MINUTE           = "minute";
    private static final String KEY_DAY_WEEK         = "day_of_week";
    private static final String KEY_ALARMS_TASK_NAME = "taskName"; //===========================

    /** Task-Alarm link table columns */
    private static final String KEY_TASKTABLE_ID    = "task_id"; //===========================
    private static final String KEY_ALARMTABLE_ID   = "alarm_id";

    /** History Table columns, some used above */
    private static final String KEY_DATE_STRING     = "date";

    /** Task Table: create statement */
    private static final String CREATE_TASK_TABLE = //===========================
            "create table " + TASK_TABLE + "(" //===========================
                    + KEY_ROWID + " integer primary key not null,"
                    + KEY_TASKNAME + " text not null" + ")"; //===========================

    /** Alarm Table: create statement */
    private static final String CREATE_ALARM_TABLE =
            "create table "         + ALARM_TABLE + "("
                    + KEY_ROWID     + " integer primary key,"
                    + KEY_INTENT    + " text,"
                    + KEY_HOUR      + " integer,"
                    + KEY_MINUTE    + " integer,"
                    + KEY_ALARMS_TASK_NAME  + " text not null," //===========================
                    + KEY_DAY_WEEK  + " integer" + ")";

    /** Task-Alarm link table: create statement */
    private static final String CREATE_ACTIVITY_ALARM_LINKS_TABLE = //===========================
            "create table "             + TASK_ALARM_LINKS + "(" //===========================
                    + KEY_ROWID         + " integer primary key not null,"
                    + KEY_TASKTABLE_ID  + " integer not null," //===========================
                    + KEY_ALARMTABLE_ID + " integer not null" + ")";

    /** Histories Table: create statement */
    private static final String CREATE_HISTORIES_TABLE =
            "CREATE TABLE "             + HISTORIES_TABLE + "("
                    + KEY_ROWID         + " integer primary key, "
                    + KEY_TASKNAME      + " text not null, " //===========================
                    + KEY_DATE_STRING   + " text, "
                    + KEY_HOUR          + " integer, "
                    + KEY_MINUTE        + " integer " + ")";

    /** Constructor */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    /** Creating tables */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE); //===========================
        db.execSQL(CREATE_ALARM_TABLE);
        db.execSQL(CREATE_ACTIVITY_ALARM_LINKS_TABLE); //===========================
        db.execSQL(CREATE_HISTORIES_TABLE);
    }

    @Override
    // TODO: change this so that updating doesn't delete old data
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE); //===========================
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_ALARM_LINKS); //===========================
        db.execSQL("DROP TABLE IF EXISTS " + HISTORIES_TABLE);
        onCreate(db);
    }

// ############################## create methods ###################################### //



    /**
     * createTask takes a task object and inserts the relevant data into the database
     *
     * @param task a model task object
     * @return the long row_id generate by the database upon entry into the database
     */
    public long createTask(Task task) { //===========================
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKNAME, task.getTaskName()); //===========================

        long task_id = db.insert(TASK_TABLE, null, values); //===========================

        return task_id;
    }

    /**
     * takes in a model alarm object and inserts a row into the database
     *      for each day of the week the alarm is meant to go off.
     * @param alarm a model alarm object
     * @param task_id the id associated with the task the alarm is for
     * @return a array of longs that are the row_ids generated by the database when the rows are inserted
     */
    public long[] createAlarm(Alarm alarm, long task_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long[] alarm_ids = new long[7];

        /** Create a separate row in the table for every day of the week for this alarm */
        int arrayPos = 0;
        for (boolean day : alarm.getDayOfWeek()) {
            if (day) {
                ContentValues values = new ContentValues();
                values.put(KEY_HOUR, alarm.getHour());
                values.put(KEY_MINUTE, alarm.getMinute());
                values.put(KEY_DAY_WEEK, arrayPos + 1);
                values.put(KEY_ALARMS_TASK_NAME, alarm.getTaskName()); //===========================

                /** Insert row */
                long alarm_id = db.insert(ALARM_TABLE, null, values);
                alarm_ids[arrayPos] = alarm_id;

                /** Link alarm to a task */
                createTaskAlarmLink(task_id, alarm_id);
            }
            arrayPos++;
        }
        return alarm_ids;
    }

    /**
     * private function that inserts a row into a table that links tasks and alarms
     *
     * @param task_id the row_id of the task that is being added to or edited
     * @param alarm_id the row_id of the alarm that is being added to the task
     * @return returns the row_id the database creates when a row is created
     */
    private long createTaskAlarmLink(long task_id, long alarm_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKTABLE_ID, task_id); //===========================
        values.put(KEY_ALARMTABLE_ID, alarm_id);

        /** Insert row */
        long taskAlarmLink_id = db.insert(TASK_ALARM_LINKS, null, values); //===========================

        return taskAlarmLink_id;
    }

    /**
     * uses a history model object to store histories in the DB
     * @param history a history model object
     */
    public void createHistory(History history) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKNAME, history.getTaskName()); //===========================
        values.put(KEY_DATE_STRING, history.getDateString());
        values.put(KEY_HOUR, history.getHourDone());
        values.put(KEY_MINUTE, history.getMinuteDone());

        /** Insert row */
        db.insert(HISTORIES_TABLE, null, values);
    }

// ############################# get methods ####################################### //

    /**
     * allows taskBox to retrieve a row from task table in Db
     * @param taskName takes in a string of the task Name //===========================
     * @return returns a task model object
     */
    public Task getTaskByName(String taskName) { //===========================
        SQLiteDatabase db = this.getReadableDatabase();

        String dbTask = "select * from " //===========================
                + TASK_TABLE        + " where " //===========================
                + KEY_TASKNAME      + " = "
                + "'"   + taskName  + "'"; //===========================

        Cursor c = db.rawQuery(dbTask, null); //===========================

        Task task = new Task(); //===========================

        if (c.moveToFirst() && c.getCount() >= 1) {
            task.setTaskName(c.getString(c.getColumnIndex(KEY_TASKNAME))); //===========================
            task.setTaskId(c.getLong(c.getColumnIndex(KEY_ROWID))); //===========================
            c.close();
        }
        return task;
    }

    /**
     * allows the taskBox to retrieve all the task rows from database
     * @return a list of task model objects
     */
    public List<Task> getAllTasks() { //===========================
        List<Task> tasks = new ArrayList<>(); //===========================
        String dbTasks = "SELECT * FROM " + TASK_TABLE; //===========================

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(dbTasks, null);

        /** Loops through all rows, adds to list */
        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setTaskName(c.getString(c.getColumnIndex(KEY_TASKNAME))); //===========================
                t.setTaskId(c.getLong(c.getColumnIndex(KEY_ROWID)));

                tasks.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return tasks;
    }


    /**
     * Allows taskBox to retrieve all Alarms linked to a Task
     * uses combineAlarms helper method
     * @param taskName string
     * @return list of alarm objects
     * @throws URISyntaxException honestly do not know why, something about alarm.getDayOfWeek()
     */
    public List<Alarm> getAllAlarmsByTask(String taskName) throws URISyntaxException {
        List<Alarm> alarmsByTask = new ArrayList<Alarm>();

        /** HINT: When reading string: '.' are not periods ex) task.rowIdNumber */
        String selectQuery = "SELECT * FROM "       +
                ALARM_TABLE         + " alarm, "    +
                TASK_TABLE          + " task, "     +
                TASK_ALARM_LINKS    + " taskAlarm WHERE "           +
                "task."             + KEY_TASKNAME      + " = '"    + taskName + "'" +
                " AND task."        + KEY_ROWID         + " = "     +
                "taskAlarm."        + KEY_TASKTABLE_ID  +
                " AND alarm."       + KEY_ROWID         + " = "     +
                "taskAlarm."        + KEY_ALARMTABLE_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Alarm al = new Alarm();
                al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)));
                al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)));
                al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)));
                al.setTaskName(c.getString(c.getColumnIndex(KEY_ALARMS_TASK_NAME)));

                alarmsByTask.add(al);
            } while (c.moveToNext());
        }

        c.close();


        return combineAlarms(alarmsByTask);
    }

    /**
     * returns all individual alarms that occur on a certain day of the week,
     * alarms returned do not know of their counterparts that occur on different days
     * @param day an integer that represents the day of week
     * @return a list of Alarms (not combined into full-model-alarms)
     */
    public List<Alarm> getAlarmsByDay(int day) {
        List<Alarm> daysAlarms = new ArrayList<Alarm>();

        String selectQuery = "SELECT * FROM "       +
                ALARM_TABLE     + " alarm WHERE "   +
                "alarm."        + KEY_DAY_WEEK      +
                " = '"          + day               + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Alarm al = new Alarm();
                al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)));
                al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)));
                al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)));
                al.setTaskName(c.getString(c.getColumnIndex(KEY_ALARMS_TASK_NAME)));

                daysAlarms.add(al);
            } while (c.moveToNext());
        }
        c.close();

        return daysAlarms;
    }


    /**
     *
     * @param alarm_id
     * @return
     * @throws URISyntaxException
     */
    public Alarm getAlarmById(long alarm_id) throws URISyntaxException {

        String dbAlarm = "SELECT * FROM "   +
                ALARM_TABLE + " WHERE "     +
                KEY_ROWID   + " = "         + alarm_id;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(dbAlarm, null);

        if (c != null)
            c.moveToFirst();

        Alarm al = new Alarm();
        al.setId(c.getInt(c.getColumnIndex(KEY_ROWID)));
        al.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)));
        al.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)));
        al.setTaskName(c.getString(c.getColumnIndex(KEY_ALARMS_TASK_NAME)));

        c.close();

        return al;
    }

    /**
     * Private helper function that combines rows in the databse back into a
     * full model-alarm with a dayOfWeek array.
     * @param dbAlarms a list of dbAlarms (not-full-alarms w/out day of week info)
     * @return a list of model-alarms
     * @throws URISyntaxException
     */
    private List<Alarm> combineAlarms(List<Alarm> dbAlarms) throws URISyntaxException {
        List<String> timesOfDay = new ArrayList<>();
        List<Alarm> combinedAlarms = new ArrayList<>();

        for (Alarm al : dbAlarms) {
            if (timesOfDay.contains(al.getStringTime())) {
                /** Add this db row to alarm object */
                for (Alarm ala : combinedAlarms) {
                    if (ala.getStringTime().equals(al.getStringTime())) {
                        int day = getDayOfWeek(al.getId());
                        boolean[] days = ala.getDayOfWeek();
                        days[day-1] = true;
                        ala.setDayOfWeek(days);
                        ala.addId(al.getId());
                    }
                }
            } else {
                /** Create new Alarm object with day of week array */
                Alarm newAlarm = new Alarm();
                boolean[] days = new boolean[7];

                newAlarm.setTaskName(al.getTaskName());
                newAlarm.setMinute(al.getMinute());
                newAlarm.setHour(al.getHour());
                newAlarm.addId(al.getId());

                int day = getDayOfWeek(al.getId());
                days[day-1] = true;
                newAlarm.setDayOfWeek(days);

                timesOfDay.add(al.getStringTime());
                combinedAlarms.add(newAlarm);
            }
        }

        Collections.sort(combinedAlarms);
        return combinedAlarms;
    }

    /**
     * Get a single taskapp.Model-Alarm
     * Used as a helper function
     */
    public int getDayOfWeek(long alarm_id) throws URISyntaxException {
        SQLiteDatabase db = this.getReadableDatabase();

        String dbAlarm = "SELECT * FROM "   +
                ALARM_TABLE + " WHERE "     +
                KEY_ROWID   + " = "         + alarm_id;

        Cursor c = db.rawQuery(dbAlarm, null);

        if (c != null)
            c.moveToFirst();

        int dayOfWeek = c.getInt(c.getColumnIndex(KEY_DAY_WEEK));
        c.close();

        return dayOfWeek;
    }

    /**
     * allows taskBox to retrieve from History table
     * @return a list of all history objects
     */
    public List<History> getHistory() {
        List<History> allHistory = new ArrayList<>();
        String dbHist = "SELECT * FROM " + HISTORIES_TABLE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(dbHist, null);

        if (c.moveToFirst()) {
            do {
                History h = new History();
                h.setTaskName(c.getString(c.getColumnIndex(KEY_TASKNAME)));
                h.setDateString(c.getString(c.getColumnIndex(KEY_DATE_STRING)));
                h.setHourDone(c.getInt(c.getColumnIndex(KEY_HOUR)));
                h.setMinuteDone(c.getInt(c.getColumnIndex(KEY_MINUTE)));

                allHistory.add(h);
            } while (c.moveToNext());
        }
        c.close();
        return allHistory;
    }

    
// ############################### delete methods##################################### //


    private void deleteTaskAlarmLinks(long alarmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_ALARM_LINKS, KEY_ALARMTABLE_ID
                + " = ?", new String[]{String.valueOf(alarmId)});
    }

    public void deleteAlarm(long alarmId) {
        SQLiteDatabase db = this.getWritableDatabase();

        /** First delete any link in TaskAlarmLink Table */
        deleteTaskAlarmLinks(alarmId);

        /* Then delete alarm */
        db.delete(ALARM_TABLE, KEY_ROWID
                + " = ?", new String[]{String.valueOf(alarmId)});
    }

    public void deleteTask(String taskName) throws URISyntaxException {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Alarm> tasksAlarms;

        /** First get all Alarms and delete them and their Task-links */
        tasksAlarms = getAllAlarmsByTask(taskName);
        for (Alarm alarm : tasksAlarms) {
            long id = alarm.getId();
            deleteAlarm(id);
        }

        /** Then delete Task */
        db.delete(TASK_TABLE, KEY_TASKNAME
                + " = ?", new String[]{taskName});
    }
}