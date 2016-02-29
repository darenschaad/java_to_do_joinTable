import org.junit.*;
import static org.junit.Assert.*;
import static org.fluentlenium.core.filter.FilterConstructor.*;

import java.util.ArrayList;

import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();
  public WebDriver getDefaultDriver() {
      return webDriver;
  }

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Test
  public void rootTest() {
      goTo("http://localhost:4567/");
      assertThat(pageSource()).contains("To-Do List");
  }


  @Test
  public void categoryIsDisplayedTest() {
    Category myCategory = new Category("Shopping");
    myCategory.save();
    goTo("http://localhost:4567");
    assertThat(pageSource()).contains("Shopping");
  }

  // @Test
  // public void allTasksDisplayDescriptionOnCategoryPage() {
  //   Category myCategory = new Category("Household chores");
  //   myCategory.save();
  //   Task firstTask = new Task("Mow the lawn");
  //   firstTask.save();
  //   Task secondTask = new Task("Do the dishes");
  //   secondTask.save();
  //   String categoryPath = String.format("http://localhost:4567/%d", myCategory.getId());
  //   goTo(categoryPath);
  //   assertThat(pageSource()).contains("Mow the lawn");
  //   assertThat(pageSource()).contains("Do the dishes");
  // }

  @Test
  public void categoryIsDeleted() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    int id = myCategory.getId();
    myCategory.deleteCategory();
    goTo("http://localhost:4567");
    assertThat(!(pageSource()).contains("Household chores"));
  }

  @Test
  public void taskIsDeleted() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    int categoryId = myCategory.getId();
    Task myTask = new Task("sweep");
    myTask.save();
    int id = myTask.getId();
    myTask.delete();
    String categoryPath = String.format("http://localhost:4567/%d", myCategory.getId());
    goTo(categoryPath);
    assertThat(!(pageSource()).contains("sweep"));
  }

}
