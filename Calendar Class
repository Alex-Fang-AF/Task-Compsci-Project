import java.time.LocalDate;
import java.util.*;
import java.util.Scanner;
public class MyCalendar {

    //Instance Variables
    private LocalDate currentDate;
    private ArrayList<DemoTask> tasksList = new ArrayList<>();

    //Constructor
    public MyCalendar() 
    {
        currentDate = LocalDate.now();
    }

    //Methods
    public void displayCurrentDate() 
    {
        System.out.println("Current date: " + currentDate);
    }
    public void addTask(DemoTask task) 
    {
        tasksList.add(task);
        System.out.println("Task added: " + task.getTaskName() + " due on " + task.getDueDate());
    }
    public void showTasksOn(LocalDate date)
    {
        System.out.println("Task(s) on " + date + ":");
        for (DemoTask task : tasksList) {
            if (task.getDueDate().equals(date)) {
                System.out.println("- " + task.getTaskName());
            }
            else{
                System.out.println("No task(s) due on this date.");
            }
        }
    }

    //Main Method
    public static void main (String[] args)
    {
        MyCalendar calendar = new MyCalendar();
        calendar.displayCurrentDate();
        DemoTask task = new DemoTask("Goon", LocalDate.of(2025, 12, 31));
        calendar.addTask(task);
        calendar.showTasksOn(LocalDate.of(2025, 12, 31));
        calendar.showTasksOn(LocalDate.of(2026, 1, 20));
    }
}
