import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import org.sql2o.*;

public class Task {
  private int id;
  private String description;
  private String dueDate;
  private boolean complete;
  private String due;

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public boolean getCompletionStatus() {
    return complete;
  }

  public String getDue() {
    return due;
  }

  public Task(String description) {
    this.description = description;
    this.complete = false;
    this.due = "";
  }

  @Override
  public boolean equals(Object otherTask){
    if (!(otherTask instanceof Task)) {
      return false;
    } else {
      Task newTask = (Task) otherTask;
      return this.getDescription().equals(newTask.getDescription()) &&
             this.getId() == newTask.getId() &&
             this.getDue() == newTask.getDue() &&
             this.getCompletionStatus() == newTask.getCompletionStatus();
    }
  }


  public static List<Task> all() {
    String sql = "SELECT * FROM tasks ORDER BY complete, due, description";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks(description, complete) VALUES (:description, :complete)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("description", description)
        .addParameter("complete", complete)
        .executeUpdate()
        .getKey();
    }
  }

  public static Task find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks where id=:id";
      Task task = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Task.class);
      return task;
    }
  }

  public void update(String description) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET description = :description WHERE id = :id";
      con.createQuery(sql)
        .addParameter("description", description)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void delete() {
    String sql = "DELETE FROM tasks WHERE id = :id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
    String joinDeleteQuery = "DELETE FROM categories_tasks WHERE task_id = :taskId";
    con.createQuery(joinDeleteQuery)
      .addParameter("taskId", id)
      .executeUpdate();
    }
  }
  public List<Task> getAllTasks() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks ORDER BY description";
      return con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetch(Task.class);
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_tasks (category_id, task_id) VALUES (:category_id, :task_id)";
      con.createQuery(sql)
        .addParameter("category_id", category.getId())
        .addParameter("task_id", this.getId())
        .executeUpdate();
    }
  }

  public ArrayList<Category> getCategories() {
    try(Connection con = DB.sql2o.open()){
      String sql = "SELECT category_id FROM categories_tasks WHERE task_id = :task_id";
      List<Integer> categoryIds = con.createQuery(sql)
        .addParameter("task_id", this.getId())
        .executeAndFetch(Integer.class);

      ArrayList<Category> categories = new ArrayList<Category>();

      for (Integer categoryId : categoryIds) {
          String taskQuery = "Select * From categories WHERE id = :categoryId ORDER BY name";
          Category category = con.createQuery(taskQuery)
            .addParameter("categoryId", categoryId)
            .executeAndFetchFirst(Category.class);
          categories.add(category);
      }
      return categories;
    }
  }

  public void removeCategory(int categoryId) {
    try(Connection con = DB.sql2o.open()){
      String sql ="DELETE FROM categories_tasks WHERE category_id =  :categoryId AND task_id = :taskId";      con.createQuery(sql)
        .addParameter("categoryId", categoryId)
        .addParameter("taskId", this.getId())
        .executeUpdate();
    }
  }

  public void deCompleteTask() {
  try(Connection con = DB.sql2o.open()){
    String sql = "UPDATE tasks SET complete = false WHERE id = :id";
    con.createQuery(sql)
      .addParameter("id", id)
      .executeUpdate();
    }
  }

  public void completeTask() {
    try(Connection con = DB.sql2o.open()){
      String sql = "UPDATE tasks SET complete = true WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void addDue(String dueDate) {
    try(Connection con = DB.sql2o.open()){
      String sql = "UPDATE tasks SET due = :dueDate WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", id)
        .addParameter("dueDate", dueDate)
        .executeUpdate();
    }
  }
}
