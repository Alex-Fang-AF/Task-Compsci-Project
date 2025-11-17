import java.time.LocalDate;
import java.util.Scanner;

public class Task{
    private String taskName;
    private LocalDate dueDate;

    //Import Scanner
        private Scanner scan = new Scanner(System.in);
    public Task(String taskName, LocalDate dueDate) {
        this.taskName = taskName;
        this.dueDate = dueDate;
    }
    public void createTask()
    {
        System.out.println("What is the name of the task? ");
        String newName = scan.nextLine();
        setTaskName(newName);
        System.out.println("What is the due date of the task? ");
        LocalDate newDate = LocalDate.parse(scan.nextLine());
        setdueDate(newDate);
    }
    public void editTask() {
        
    }   

    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String n){
        this.taskName = n;
    }
    
    public void setdueDate(LocalDate d){
        this.dueDate = d;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
}
