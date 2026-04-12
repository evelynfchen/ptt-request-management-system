# Group_SEIT_AE2_GroupReport

## Team Number and Member Contributions

### Team Number

### Team Members and Class Responsibilities
| Member   | Class Responsibilities                                      |
|:---------|:------------------------------------------------------------|
| Member1  |                                                             |
| Member2  |                                                             |
| Member3  |                                                             |
| Member4  |                                                             |
| Member5  |                                                             |
| Tang Hui | design explanation outline and the final report integration |

## User Stories

### US1 - Submit Request
**Title:** Submit Request

**User Story:**  
As a Course Director, I want to submit a teaching request with course details and required skills, so that suitable PTT candidates can later be identified.

**Acceptance Criteria:**
1. The system validates mandatory fields such as `courseCode`, `requiredSkills`, and `budget`.
2. A new `TeachingRequest` object is created with status **PENDING**.
3. The request is stored in the system for later candidate matching.

**Priority:** Must-have

### US2 - Match PTT Candidates
**Title:** Match PTT Candidates

**User Story:**  
As an Administrator, I want the system to find suitable PTT members based on the required skills in a teaching request, so that appropriate candidates can be identified efficiently.

**Acceptance Criteria:**
1. The system compares `TeachingRequest.requiredSkills` with `PTTMember.skills`.
2. Only PTT members with relevant matching skills are returned.
3. The matched candidate list is attached to the `TeachingRequest`.
4. The request status changes from **PENDING** to **MATCHED** when matching is completed.

**Priority:** Must-have

### US3 - Record PTT Training
**Title:** Record PTT Training

**User Story:**  
As an Administrator, I want to record training information for PTT members, so that their training status and history are available for future allocation.

**Acceptance Criteria:**
1. Training records include at least a **topic** and a **date**.
2. When training is recorded successfully, `PTTMember.isTrained` is updated to **true**.
3. Historical training records can be viewed later for each PTT member.

**Priority:** Could-have

### US4 - System Data Persistence
**Title:** System Data Persistence

**User Story:**  
As an Administrator, I want the system to load and save application data from a file, so that information is preserved across sessions without requiring a real database.

**Acceptance Criteria:**
1. A file such as `data.txt` is read when the program starts.
2. The system rebuilds in-memory objects from the stored file data.
3. All updates are written back to the file before the program exits.
4. The file format is validated to reduce the risk of corrupted or invalid data.

**Priority:** Must-have

## Sequence Diagrams

### Sequence Diagram – User Story 1
![User Story 1](../uml/diagram1_v2.png)
### Sequence Diagram – User Story 2
![User Story 2](../uml/diagram2_v2.png)
### Sequence Diagram – User Story 3
![User Story 3](../uml/diagram3_v2.png)
### Sequence Diagram – User Story 4
![User Story 4](../uml/diagram4_v2.png)
## Class Structure Diagram
![Class Structure Diagram](../uml/class_diagram_final.png)
## Design Discussion

This system manages the assignment of Part-Time Teaching (PTT) staff to courses, matching members to teaching requests based on their skills. The design prioritises clarity, extensibility, and low coupling through a combination of well-established patterns and deliberate structural choices.


### Inheritance Hierarchy and the Abstract `User`

The `User` class serves as an abstract base for `Administrator`, `CourseDirector`, and `PTTMember`. This is a straightforward application of **generalisation**, capturing shared identity attributes (`guid`, `name`) in one place and avoiding repetition across subclasses. Only `PTTMember` carries additional domain-specific state — skills, training records, and trained status — keeping the hierarchy shallow and purposeful rather than over-engineered.


### Strategy Pattern for Matching

The most deliberate design decision is the application of the **Strategy Pattern** to candidate matching. The `MatchingStrategy` interface declares a single `match(TeachingRequest, List<PTTMember>)` method, and `SkillMatchingStrategy` provides the concrete implementation. A `MatchingEngine` holds a reference to whichever strategy is injected at construction time.

This decouples the *algorithm* from the *context that uses it*. Adding a new matching rule — say, ranking by availability, budget fit, or training history — requires only a new class implementing `MatchingStrategy`, with zero changes to `MatchingEngine` or any calling code. This directly satisfies the **Open/Closed Principle**: the system is open for extension but closed for modification.


### Singleton for the Database

`SimpleDatabase` uses the **Singleton Pattern** with `synchronized` access to ensure only one in-memory store exists across the application lifetime. This is appropriate here because the flat-file persistence model (`data/data.txt`) would produce race conditions or duplicate state with multiple instances. The trade-off is reduced testability — a singleton is hard to swap for a mock — but for the scale of this system, the simplicity outweighs that concern.


### Enum for Request Status

Using a `RequestStatus` enum (`PENDING`, `MATCHED`) rather than a plain string or boolean provides type safety and makes invalid states unrepresentable at compile time. It also makes the persistence layer's `parseStatus` method straightforward and resilient: unknown values gracefully fall back to `PENDING` rather than crashing.


### Reducing Coupling via the `TeachingRequest` Aggregate

`TeachingRequest` bundles a `Course`, a list of required skills, matched candidates, and a status into a single cohesive aggregate. Services interact with this one object rather than passing multiple loose parameters. This reduces the surface area of inter-component dependencies and means that extending a request (e.g., adding a deadline or priority) is localised to one class.


### Flat-File Persistence with Section Parsing

`SimpleDatabase` reads and writes a structured plain-text file divided into `[COURSES]`, `[PTT_MEMBERS]`, and `[REQUESTS]` sections. The parsing is handled by an inner `Section` enum with a `fromHeader` factory method, keeping section-detection logic encapsulated and easy to extend. Invalid lines are skipped gracefully with a console warning rather than aborting the load — a pragmatic choice for a lightweight persistence layer.


### Separation of Concerns

The three packages — `entity`, `service`, and `persistence` — enforce a clear layered structure. Entities carry state and domain logic (e.g., `hasAllSkills`). Services encapsulate algorithmic behaviour. Persistence handles I/O. Neither the entity layer nor the service layer has any dependency on `SimpleDatabase`, meaning matching logic can be unit-tested in isolation without touching the file system.


### Summary

The design makes targeted, justified use of Strategy, Singleton, and Enum patterns, combined with a clean package structure to minimise coupling. The result is a system that is easy to extend — particularly in its matching logic — without requiring widespread changes, and where each component has a single, well-defined responsibility.

## Screenshots

## Retrospective

### What Went Well

### What Was Challenging

## Code
### Table of Contents

1. [Entity Package](#entity-package)
    - [User.java](#userjava)
    - [Administrator.java](#administratorjava)
    - [CourseDirector.java](#coursedirectorjava)
    - [PTTMember.java](#pttmemberjava)
    - [Course.java](#coursejava)
    - [TeachingRequest.java](#teachingrequestjava)
    - [TrainingRecord.java](#trainingrecordjava)
    - [RequestStatus.java](#requeststatusjava)

2. [Service Package](#service-package)
    - [MatchingStrategy.java](#matchingstrategyjava)
    - [SkillMatchingStrategy.java](#skillmatchingstrategyjava)
    - [MatchingEngine.java](#matchingenginejava)
    - [RequestManager.java](#requestmanagerjava)
    - [TrainingManager.java](#trainingmanagerjava)

3. [Persistence Package](#persistence-package)
    - [SimpleDatabase.java](#simpledatabasejava)

4. [Test & Main](#test--main)
    - [EntityTest.java](#entitytestjava)
    - [Main.java](#mainjava)

---

## Entity Package

### User.java

```java
package entity;

public abstract class User {
    private String guid;
    private String name;

    public User(String guid, String name) {
        this.guid = guid;
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

### Administrator.java

```java
package entity;

public class Administrator extends User {

    public Administrator(String guid, String name) {
        super(guid, name);
    }
}
```

### CourseDirector.java

```java
package entity;

public class CourseDirector extends User {

    public CourseDirector(String guid, String name) {
        super(guid, name);
    }
}
```

### PTTMember.java

```java
package entity;

import java.util.ArrayList;
import java.util.List;

public class PTTMember extends User {
    private List<String> skills;
    private boolean isTrained;
    private List<TrainingRecord> trainingRecords;

    public PTTMember(String guid, String name) {
        super(guid, name);
        this.skills = new ArrayList<>();
        this.isTrained = false;
        this.trainingRecords = new ArrayList<>();
    }

    public PTTMember(String guid, String name, List<String> skills) {
        super(guid, name);
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        this.isTrained = false;
        this.trainingRecords = new ArrayList<>();
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
    }

    public boolean isTrained() {
        return isTrained;
    }

    public void setTrained(boolean trained) {
        isTrained = trained;
    }

    public List<TrainingRecord> getTrainingRecords() {
        return trainingRecords;
    }

    public void setTrainingRecords(List<TrainingRecord> trainingRecords) {
        this.trainingRecords = trainingRecords != null ? new ArrayList<>(trainingRecords) : new ArrayList<>();
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.isBlank() && !this.skills.contains(skill)) {
            this.skills.add(skill);
        }
    }

    public void addTrainingRecord(TrainingRecord record) {
        if (record != null) {
            this.trainingRecords.add(record);
            this.isTrained = true;
        }
    }
    public boolean hasSkill(String skill) {
        return skill != null && !skill.isBlank() && skills.contains(skill);
    }

    public boolean hasAllSkills(List<String> requiredSkills) {
        if (requiredSkills == null) {
            return false;
        }
        return skills.containsAll(requiredSkills);
    }
}
```

### Course.java

```java
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
```

### TeachingRequest.java

```java
package entity;

import java.util.ArrayList;
import java.util.List;

public class TeachingRequest {
    private Course course;
    private List<String> requiredSkills;
    private List<PTTMember> matchedCandidates;
    private RequestStatus status;

    public TeachingRequest(Course course, List<String> requiredSkills) {
        this.course = course;
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        this.matchedCandidates = new ArrayList<>();
        this.status = RequestStatus.PENDING;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }

    public List<PTTMember> getMatchedCandidates() {
        return matchedCandidates;
    }

    public void setMatchedCandidates(List<PTTMember> matchedCandidates) {
        this.matchedCandidates = matchedCandidates != null ? new ArrayList<>(matchedCandidates) : new ArrayList<>();
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void addMatchedCandidate(PTTMember candidate) {
        if (candidate != null && !this.matchedCandidates.contains(candidate)) {
            this.matchedCandidates.add(candidate);
            this.status = RequestStatus.MATCHED;
        }
    }

    public boolean isMatched() {
        return matchedCandidates != null && !matchedCandidates.isEmpty();
    }

    public void addRequiredSkill(String skill) {
        if (skill != null && !skill.isBlank()) {
            this.requiredSkills.add(skill);
        }
    }

    public void clearMatchedCandidates() {
        this.matchedCandidates.clear();
    }
}
```

### TrainingRecord.java

```java
package entity;

public class TrainingRecord {
    private String topic;
    private String date;

    public TrainingRecord(String topic, String date) {
        this.topic = topic;
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
```

### RequestStatus.java

```java
package entity;

public enum RequestStatus {
    PENDING,
    MATCHED
}
```

---

## Service Package

### MatchingStrategy.java

```java
package service;

import entity.PTTMember;
import entity.TeachingRequest;
import java.util.List;

public interface MatchingStrategy {
    List<PTTMember> match(TeachingRequest request, List<PTTMember> allStaff);
}
```

### SkillMatchingStrategy.java

```java
package service;

import entity.PTTMember;
import entity.TeachingRequest;
import java.util.ArrayList;
import java.util.List;

public class SkillMatchingStrategy implements MatchingStrategy {

    @Override
    public List<PTTMember> match(TeachingRequest request, List<PTTMember> allStaff) {
        List<PTTMember> matches = new ArrayList<>();

        if (request == null || allStaff == null) {
            return matches;
        }

        List<String> required = request.getRequiredSkills();
        if (required == null || required.isEmpty()) {
            return matches;
        }


        for (PTTMember staff : allStaff) {
            if (staff.hasAllSkills(required)) {
                matches.add(staff);
            }
        }

        return matches;
    }
}
```

### MatchingEngine.java

```java
package service;

import entity.PTTMember;
import entity.TeachingRequest;
import java.util.Objects;
import java.util.List;

public class MatchingEngine {
    private MatchingStrategy strategy;

    public MatchingEngine(MatchingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
    }

    public List<PTTMember> matchCandidates(TeachingRequest request, List<PTTMember> allStaff) {
        return strategy.match(request, allStaff);
    }
}
```

### RequestManager.java

```java
package service;

import entity.TeachingRequest;
import entity.RequestStatus;
import persistence.SimpleDatabase;
import java.util.ArrayList;
import java.util.List;

public class RequestManager {
    public void createRequest(TeachingRequest request) {
        if (request == null) {
            return;
        }

        SimpleDatabase database = SimpleDatabase.getInstance();
        List<TeachingRequest> requests = database.getRequests();
        if (requests == null) {
            requests = new ArrayList<>();
            database.setRequests(requests);
        }

        requests.add(request);
    }

    public void approveRequest(TeachingRequest request) {
        if (request != null) {
            request.setStatus(RequestStatus.MATCHED);
        }
    }

    public List<TeachingRequest> getAllRequests() {
        List<TeachingRequest> requests = SimpleDatabase.getInstance().getRequests();
        return requests != null ? requests : new ArrayList<>();
    }
}
```

### TrainingManager.java

```java
package service;

import entity.PTTMember;
import entity.TrainingRecord;
import java.time.LocalDate;

public class TrainingManager {
    public void recordTraining(PTTMember staff, String newSkill) {
        if (staff != null && newSkill != null && !newSkill.isBlank()) {
            staff.addSkill(newSkill);
            staff.addTrainingRecord(new TrainingRecord(newSkill, LocalDate.now().toString()));
        }
    }
}
```

---

## Persistence Package

### SimpleDatabase.java

```java
package persistence;

import entity.Course;
import entity.PTTMember;
import entity.RequestStatus;
import entity.TeachingRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleDatabase {

    private static final String DATA_FILE = "data/data.txt";

    private static SimpleDatabase instance;

    private List<Course> courses;
    private List<PTTMember> pttMembers;
    private List<TeachingRequest> requests;

    private SimpleDatabase() {
        this.courses = new ArrayList<>();
        this.pttMembers = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public static synchronized SimpleDatabase getInstance() {
        if (instance == null) {
            instance = new SimpleDatabase();
        }
        return instance;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<PTTMember> getPttMembers() {
        return pttMembers;
    }

    public void setPttMembers(List<PTTMember> pttMembers) {
        this.pttMembers = pttMembers;
    }

    public List<TeachingRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<TeachingRequest> requests) {
        this.requests = requests;
    }

    public void loadData() {
        ensureDataFileExists();

        courses.clear();
        pttMembers.clear();
        requests.clear();

        Section currentSection = Section.NONE;

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String rawLine;
            while ((rawLine = br.readLine()) != null) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                Section section = Section.fromHeader(line);
                if (section != null) {
                    currentSection = section;
                    continue;
                }

                try {
                    switch (currentSection) {
                        case COURSES:
                            Course course = parseCourseLine(line);
                            if (course != null) {
                                addOrUpdateCourse(course);
                            }
                            break;
                        case PTT_MEMBERS:
                            PTTMember member = parseMemberLine(line);
                            if (member != null) {
                                pttMembers.add(member);
                            }
                            break;
                        case REQUESTS:
                            TeachingRequest request = parseRequestLine(line);
                            if (request != null) {
                                requests.add(request);
                            }
                            break;
                        case NONE:
                        default:
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    public void saveData() {
        ensureDataFileExists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            bw.write("[COURSES]");
            bw.newLine();
            for (Course course : courses) {
                bw.write(serializeCourse(course));
                bw.newLine();
            }
            bw.newLine();

            bw.write("[PTT_MEMBERS]");
            bw.newLine();
            for (PTTMember member : pttMembers) {
                bw.write(serializeMember(member));
                bw.newLine();
            }
            bw.newLine();

            bw.write("[REQUESTS]");
            bw.newLine();
            for (TeachingRequest request : requests) {
                bw.write(serializeRequest(request));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    public Course findCourseByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Course course : courses) {
            if (code.equals(course.getCourseCode())) {
                return course;
            }
        }
        return null;
    }

    public PTTMember findMemberById(String id) {
        if (id == null) {
            return null;
        }
        for (PTTMember member : pttMembers) {
            if (id.equals(member.getGuid())) {
                return member;
            }
        }
        return null;
    }

    public TeachingRequest findRequestByCourse(String courseCode) {
        if (courseCode == null) {
            return null;
        }
        for (TeachingRequest request : requests) {
            Course course = request.getCourse();
            if (course != null && courseCode.equals(course.getCourseCode())) {
                return request;
            }
        }
        return null;
    }

    public void addCourse(Course course) {
        if (course != null) {
            courses.add(course);
        }
    }

    public void addPTTMember(PTTMember member) {
        if (member != null) {
            pttMembers.add(member);
        }
    }

    public void addRequest(TeachingRequest request) {
        if (request != null) {
            requests.add(request);
        }
    }

    private void ensureDataFileExists() {
        File file = new File(DATA_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                System.out.println("Failed to create data directory: " + parent.getPath());
            }
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Failed to create data file: " + file.getPath());
                }
            } catch (IOException e) {
                System.out.println("Failed to create data file: " + e.getMessage());
            }
        }
    }

    private Course parseCourseLine(String line) {
        String[] parts = splitLine(line, 2);
        if (parts == null) {
            return null;
        }
        String code = parts[0].trim();
        String budgetText = parts[1].trim();
        if (code.isEmpty()) {
            return null;
        }
        try {
            double budget = Double.parseDouble(budgetText);
            return new Course(code, budget);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private PTTMember parseMemberLine(String line) {
        String[] parts = splitLine(line, 4);
        if (parts == null) {
            return null;
        }
        String guid = parts[0].trim();
        String name = parts[1].trim();
        String skillsText = parts[2].trim();
        String availableText = parts[3].trim();

        if (guid.isEmpty() || name.isEmpty()) {
            return null;
        }

        List<String> skills = splitCsv(skillsText);
        Boolean available = parseBooleanStrict(availableText);
        if (available == null) {
            return null;
        }

        PTTMember member = new PTTMember(guid, name, skills);
        member.setTrained(available);
        return member;
    }

    private TeachingRequest parseRequestLine(String line) {
        String[] parts = splitLine(line, 4);
        if (parts == null) {
            return null;
        }
        String courseCode = parts[0].trim();
        String skillsText = parts[1].trim();
        String statusText = parts[2].trim();
        String matchedMemberId = parts[3].trim();

        if (courseCode.isEmpty()) {
            return null;
        }

        Course course = findCourseByCode(courseCode);
        if (course == null) {
            course = new Course(courseCode, 0);
            courses.add(course);
        }

        List<String> requiredSkills = splitCsv(skillsText);
        TeachingRequest request = new TeachingRequest(course, requiredSkills);
        request.setStatus(parseStatus(statusText));

        if (!matchedMemberId.isEmpty()) {
            PTTMember member = findMemberById(matchedMemberId);
            if (member != null) {
                request.addMatchedCandidate(member);
            }
        }

        return request;
    }

    private void addOrUpdateCourse(Course course) {
        Course existing = findCourseByCode(course.getCourseCode());
        if (existing == null) {
            courses.add(course);
        } else {
            existing.setBudget(course.getBudget());
        }
    }

    private String serializeCourse(Course course) {
        if (course == null) {
            return "";
        }
        return course.getCourseCode() + "|" + course.getBudget();
    }

    private String serializeMember(PTTMember member) {
        if (member == null) {
            return "";
        }
        String skillsText = String.join(",", safeSkills(member.getSkills()));
        return member.getGuid() + "|" + member.getName() + "|" + skillsText + "|" + member.isTrained();
    }

    private String serializeRequest(TeachingRequest request) {
        if (request == null) {
            return "";
        }
        String courseCode = request.getCourse() == null ? "" : request.getCourse().getCourseCode();
        String skillsText = String.join(",", safeSkills(request.getRequiredSkills()));
        String statusText = request.getStatus() == null ? RequestStatus.PENDING.name() : request.getStatus().name();
        String matchedMemberId = "";
        List<PTTMember> matched = request.getMatchedCandidates();
        if (matched != null && !matched.isEmpty() && matched.get(0) != null) {
            matchedMemberId = matched.get(0).getGuid();
        }
        return courseCode + "|" + skillsText + "|" + statusText + "|" + matchedMemberId;
    }

    private String[] splitLine(String line, int expectedParts) {
        if (line == null) {
            return null;
        }
        String[] parts = line.split("\|", -1);
        if (parts.length != expectedParts) {
            return null;
        }
        return parts;
    }

    private List<String> splitCsv(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = text.split(",");
        List<String> results = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                results.add(trimmed);
            }
        }
        return results;
    }

    private List<String> safeSkills(List<String> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills;
    }

    private RequestStatus parseStatus(String text) {
        if (text == null || text.trim().isEmpty()) {
            return RequestStatus.PENDING;
        }
        try {
            return RequestStatus.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
        }
    }

    private Boolean parseBooleanStrict(String text) {
        if (text == null) {
            return null;
        }
        String normalized = text.trim().toLowerCase();
        if ("true".equals(normalized)) {
            return Boolean.TRUE;
        }
        if ("false".equals(normalized)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private enum Section {
        NONE,
        COURSES,
        PTT_MEMBERS,
        REQUESTS;

        private static Section fromHeader(String line) {
            if ("[COURSES]".equalsIgnoreCase(line)) {
                return COURSES;
            }
            if ("[PTT_MEMBERS]".equalsIgnoreCase(line)) {
                return PTT_MEMBERS;
            }
            if ("[REQUESTS]".equalsIgnoreCase(line)) {
                return REQUESTS;
            }
            return null;
        }
    }
}
```

---

## Test & Main

### EntityTest.java

```java
import entity.Course;
import entity.PTTMember;
import entity.TeachingRequest;
import entity.TrainingRecord;
import entity.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class EntityTest {

    public static void main(String[] args) {

        // create course
        Course course = new Course("COMP101", 2000);

        // required skills for request
        List<String> requiredSkills = new ArrayList<>();
        requiredSkills.add("Java");

        // create teaching request
        TeachingRequest request = new TeachingRequest(course, requiredSkills);

        // create members
        PTTMember member = new PTTMember("M001", "Alice");
        member.addSkill("Java");

        PTTMember member2 = new PTTMember("M002", "Bob");
        member2.addSkill("Python");

        List<PTTMember> candidates = new ArrayList<>();
        candidates.add(member);
        candidates.add(member2);

        // match candidates
        for (PTTMember candidate : candidates) {
            if (candidate.hasAllSkills(request.getRequiredSkills())) {
                request.addMatchedCandidate(candidate);
                request.setStatus(RequestStatus.MATCHED);
            }
        }

        // before training
        System.out.println("Before training - trained: " + member.isTrained());

        // add training record
        member.addTrainingRecord(new TrainingRecord("Python", "2026-03-15"));

        // print results
        System.out.println("Course: " + request.getCourse().getCourseCode());
        System.out.println("Matched: " + request.isMatched());
        System.out.println("Status: " + request.getStatus());
        System.out.println("Matched candidates:");
        for (PTTMember c : request.getMatchedCandidates()) {
            System.out.println("- " + c.getName());
        }
        System.out.println("Training count: " + member.getTrainingRecords().size());
        System.out.println("After training - trained: " + member.isTrained());
    }
}
```

### Main.java

```java
import entity.Course;
import entity.PTTMember;
import entity.TeachingRequest;
import entity.RequestStatus;
import persistence.SimpleDatabase;
import service.*;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final RequestManager requestManager = new RequestManager();
    private static final TrainingManager trainingManager = new TrainingManager();

    private static final MatchingStrategy strategy = new SkillMatchingStrategy();
    private static final MatchingEngine matchingEngine = new MatchingEngine(strategy);

    private static final SimpleDatabase db = SimpleDatabase.getInstance();

    public static void main(String[] args) {

        db.loadData();

        System.out.println("===== SEIT AE2 Part Time Teacher System =====");

        while (true) {

            printMenu();

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    createRequest();
                    break;

                case "2":
                    matchCandidates();
                    break;

                case "3":
                    recordTraining();
                    break;

                case "4":
                    saveData();
                    break;

                case "5":
                    db.saveData();
                    System.out.println("System exit.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void printMenu() {

        System.out.println("\n----------------------");
        System.out.println("1. Create Request");
        System.out.println("2. Match Candidates");
        System.out.println("3. Record Training");
        System.out.println("4. Save Data");
        System.out.println("5. Exit");
        System.out.println("----------------------");
        System.out.print("Choose option: ");
    }


    private static void createRequest() {

        System.out.print("Course code: ");
        String courseCode = scanner.nextLine();

        System.out.print("Course budget: ");
        String budgetInput = scanner.nextLine();
        double budget = 0;
        try {
            budget = Double.parseDouble(budgetInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid budget, defaulting to 0.");
        }
        Course course = new Course(courseCode, budget);

        System.out.print("Required skills (comma separated): ");
        String skillLine = scanner.nextLine();

        List<String> skills = new ArrayList<>();

        if (!skillLine.isEmpty()) {
            String[] arr = skillLine.split(",");
            for (String s : arr) {
                skills.add(s.trim());
            }
        }

        TeachingRequest request = new TeachingRequest(course, skills);

        requestManager.createRequest(request);

        System.out.println("Request created.");
    }

    private static void matchCandidates() {

        List<TeachingRequest> requests = requestManager.getAllRequests();
        List<PTTMember> staff = db.getPttMembers();

        if (requests == null || requests.isEmpty()) {
            System.out.println("No teaching requests found.");
            return;
        }

        if (staff == null || staff.isEmpty()) {
            System.out.println("No staff available.");
            return;
        }

        for (TeachingRequest request : requests) {

            List<PTTMember> matches =
                    matchingEngine.matchCandidates(request, staff);

            request.setMatchedCandidates(matches);
            if (!matches.isEmpty()) {
                request.setStatus(RequestStatus.MATCHED);
            }

            System.out.println("\nMatched candidates for course: " + request.getCourse().getCourseCode());

            if (matches.isEmpty()) {
                System.out.println("No match found.");
            } else {
                for (PTTMember m : matches) {
                    System.out.println("- " + m.getName());
                }
            }
        }
    }


    private static void recordTraining() {

        System.out.print("PTT Member ID: ");
        String id = scanner.nextLine();

        System.out.print("Training course: ");
        String trainingTopic = scanner.nextLine();

        PTTMember member = db.findMemberById(id);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        trainingManager.recordTraining(member, trainingTopic);

        System.out.println("Training recorded.");
    }


    private static void saveData() {

        db.saveData();

        System.out.println("Data saved.");
    }
}
```

---

