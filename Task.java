import java.util.Scanner;
import java.time.LocalDate;

Public class Task{
    private String taskName;
    private LocalDate dueDate;

    //Import Scanner
    Scanner scan = new Scanner(System.in);
    public DemoTask(String taskName, LocalDate dueDate) {
        this.taskName = taskName;
        this.dueDate = dueDate;
    }
    public void createTask()
    {
        System.out.println("What is the name of the task? ");
        String newName = scan.nextLine;
        setTaskName(newName);
        System.out.println("What is the due date of the task? ");
        LocalDate newDate = scan.next;
        setdueDate(newDate);
    }
    public void editTask()
    {
        
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String n){
        this.taskName = n;
    }
    public void setdueDate(String d){
        this.dueDate = d;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
}
