package dailyreminderapp.Model;

import java.util.Comparator;

/**
 * Created by Laura on 5/9/15.
 * This Comparator allows the tasks to be alphabetized by name
 */
public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2){

        String firstName = task1.getTaskName();
        String secondName = task2.getTaskName();
        return firstName.compareTo(secondName);
    }
}
