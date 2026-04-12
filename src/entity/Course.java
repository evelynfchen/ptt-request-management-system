package entity;

public class Course {
    private String courseCode;
    private double budget;

    public Course(String courseCode, double budget) {
        this.courseCode = courseCode;
        this.budget = budget;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}