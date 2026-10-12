# Part-Time Teaching (PTT) Request Management System

A command-line Java application for managing part-time teaching requests: creating requests with required skills, matching them against available staff, recording training, and persisting data to a flat file.

This was a **six-person team project** (University of Glasgow, MSc Software Development). This repository is my personal portfolio copy of the team's submitted application — the full codebase is included so it still runs end-to-end, but the sections below describe specifically **what I personally built**, not the team's work as a whole. For the detailed authorship evidence (git blame/log breakdowns, line counts, commit references), see [CONTRIBUTIONS.md](CONTRIBUTIONS.md).

## What I built

- **Domain modelling and encapsulation** — I designed and implemented the core entity classes from scratch: [`PTTMember`](src/entity/PTTMember.java), [`TeachingRequest`](src/entity/TeachingRequest.java), [`TrainingRecord`](src/entity/TrainingRecord.java), [`RequestStatus`](src/entity/RequestStatus.java), [`Course`](src/entity/Course.java), [`User`](src/entity/User.java), [`Administrator`](src/entity/Administrator.java), and [`CourseDirector`](src/entity/CourseDirector.java). All fields are private, exposed only through getters/setters and purpose-built methods.
- **Entity-level validation and invariants** — `PTTMember` and `TeachingRequest` guard their own state: `addSkill()` and `addMatchedCandidate()` both reject duplicates, and `addMatchedCandidate()` automatically transitions the request's status to `MATCHED` the moment a candidate is added, so calling code can't add a match and forget to update status.
- **Reusable skill-validation logic** — `PTTMember.hasAllSkills()` is the method a teammate's matching algorithm ([`SkillMatchingStrategy`](src/service/SkillMatchingStrategy.java)) calls to check whether a staff member covers a request's required skills. I implemented the validation method itself; the surrounding Strategy-pattern matching algorithm that calls it was written by a teammate.
- **Defensive copying of incoming data** — constructors and setters in `PTTMember` and `TeachingRequest` copy any `List` passed in (`new ArrayList<>(skills)`, etc.) rather than storing the caller's reference directly, so the entity isn't left holding a list the caller can still mutate from outside. This protection is scoped to constructor/setter inputs — see the note in CONTRIBUTIONS.md on what it does and doesn't cover.
- **CLI integration** — I extended the command-line flow in [`Main.java`](src/Main.java) (budget input with validation/fallback, wiring the auto-status-update into the "match candidates" menu option, minor fixes), on top of the menu-driven CLI structure a teammate built.
- **Unit-level testing** — [`EntityTest.java`](src/EntityTest.java) is a manual test harness I wrote that exercises the entity layer directly: creating a request, matching a candidate by skill, confirming the status transition, and recording training.

## What I did not build

To keep attribution accurate: the persistence layer ([`SimpleDatabase`](src/persistence/SimpleDatabase.java)), the Strategy Pattern interface and matching algorithm itself ([`MatchingStrategy`](src/service/MatchingStrategy.java), [`MatchingEngine`](src/service/MatchingEngine.java), [`SkillMatchingStrategy`](src/service/SkillMatchingStrategy.java)), [`RequestManager`](src/service/RequestManager.java), [`TrainingManager`](src/service/TrainingManager.java), and the overall CLI menu structure in `Main.java` were primarily built by teammates. Full attribution is in [CONTRIBUTIONS.md](CONTRIBUTIONS.md).

## System design (whole application)

The points below describe the team's overall system, not just my contribution:

- **Layered architecture**
  - Entity layer: domain models (`TeachingRequest`, `PTTMember`, etc.)
  - Service layer: business logic (`MatchingEngine`, `RequestManager`, `TrainingManager`)
  - Persistence layer: file-based data storage (`SimpleDatabase`)
- **Design patterns**
  - Strategy Pattern: pluggable skill-based matching logic (`MatchingStrategy` / `SkillMatchingStrategy`)
  - Singleton Pattern: shared access to the persistence component (`SimpleDatabase.getInstance()`)
- **Data persistence**
  - File-based storage via `SimpleDatabase`, with load/save of application state to `data/data.txt`

## Technologies

Java, Object-Oriented Programming, UML (class & sequence diagrams, see `UML/`), Git/GitHub (team collaboration).

## Running the application

Requires a JDK (11+).

```bash
# compile
javac -d out $(find src -name "*.java")

# run the interactive CLI (from the project root, so it can find data/data.txt)
java -cp out Main

# or run the standalone entity test harness
java -cp out EntityTest
```

Alternatively, open the project in IntelliJ IDEA and run `Main` directly.

From the CLI you can create requests, match candidates, record training, and save/load data.

## Screenshots

These screenshots demonstrate the key functionality of the system, including request creation, candidate matching, and system output.

### System start
![System Start](screenshots/figure1_system_start.png)

### Matching process
![Matching Process](screenshots/figure3_matching.png)

### System output
![System Output](screenshots/running_output.png)

## Data file format

Path: `data/data.txt`

```text
[COURSES]
courseCode|budget

[PTT_MEMBERS]
guid|name|skills|available

[REQUESTS]
courseCode|requiredSkills|status|matchedMember
```

Example:
```text
[COURSES]
CS101|1000
CS102|1200

[PTT_MEMBERS]
M001|Alice|Java,SQL|true
M002|Bob|Python|false

[REQUESTS]
CS101|Java|MATCHED|M001
CS102|Python|PENDING|
```

Rules:
- Sections are identified by the header lines in brackets.
- Fields are separated by `|`.
- Skills are comma-separated within the skills field.
- Empty lines are allowed.
- Lines with the wrong number of fields are ignored during load.
- `available` must be `true` or `false`.
- `status` must be `PENDING` or `MATCHED`.
- `matchedMember` may be empty.
