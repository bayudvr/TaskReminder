package dailyreminderapp.ViewController;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import dailyreminderapp.Model.Alarm;
import dailyreminderapp.Model.Task;
import dailyreminderapp.Model.TaskBox;

import dailyreminderapp.Model.TaskComparator;
import dailyreminderapp.ViewController.adapter.ExpandableListAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import teamqitalach.taskapp.R;

/**
 * This activity handles the view and controller of the taskbox page, where
 * the user can view alarms by tasks and edit or delete an alarm
 */
public class TaskBoxActivity extends ActionBarActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    // This data structure allows us to get the ids of the alarms we want to edit
    // and store them in the tempId in the task box model. The structure is similar
    // to the struture of listDataChild.
    List<List<List<Long>>> alarmIDData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Task Box");
        setContentView(R.layout.activity_task_box);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        try {
            prepareListData();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                TaskBox taskBox = new TaskBox();
                taskBox.setTempIds(alarmIDData.get(groupPosition).get(childPosition));

                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_box, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }


    /** Preparing the list data */
    private void prepareListData() throws URISyntaxException {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        alarmIDData = new ArrayList<List<List<Long>>>();

        TaskBox taskbox = new TaskBox();
        List<Task> tasks = taskbox.getTasks(this);
        Collections.sort(tasks, new TaskComparator());

        for (Task task: tasks){
            String name = task.getTaskName();
            listDataHeader.add(name);
            List<String> times = new ArrayList<String>();
            List<Alarm> alarms = taskbox.getAlarmByTask(this.getBaseContext(), name);
            List<List<Long>> ids = new ArrayList<List<Long>>();

            for (Alarm alarm :alarms){
                String time = alarm.getStringTime() + daysList(alarm);
                times.add(time);
                ids.add(alarm.getIds());
            }
            alarmIDData.add(ids);
            listDataChild.put(name, times);
        }
    }

    /**
     * Helper function to obtain a string of the days of the week
     * that can be used as a boolean list
     */
    private String daysList(Alarm alarm){
        String days = "#";
        for(int i=0; i<7; i++){
            if (alarm.getDayOfWeek()[i])
                days += "1";
            else
                days += "0";
        }
        return days;
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
    }
}