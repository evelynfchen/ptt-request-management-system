# Class Responsibilities

This document describes the responsibility of each class in the system.

The system is organised into four layers:

- Entity layer
- Service layer
- Persistence layer
- Controller layer

---

# 1. Entity Layer

The entity layer stores the main data used in the system.

## Course

Represents a course.

Responsibilities:
- Store course code
- Store course budget
- Provide getters and setters

---

## PTTMember

Represents a part-time teacher.

Responsibilities:
- Store member ID and basic information
- Store the skills of the teacher
- Store training records

---

## TeachingRequest

Represents a request for teaching support.

Responsibilities:
- Store the course related to the request
- Store required skills
- Store matched candidates
- Store request status

---

## RequestStatus

Defines the status of a teaching request.

Responsibilities:
- Represent request states
- Example states include PENDING and MATCHED

---

## TrainingRecord

Represents a training record of a teacher.

Responsibilities:
- Store training information
- Link the training with a PTT member

---

## Administrator

Represents an administrator of the system.

Responsibilities:
- Manage system operations
- Monitor requests and staff

---

## CourseDirector

Represents a course director.

Responsibilities:
- Create teaching requests
- Manage course related requests

---

## User

Base class for system users.

Responsibilities:
- Store basic user information

---

# 2. Service Layer

The service layer contains the main system logic.

## RequestManager

Handles teaching requests.

Responsibilities:
- Create teaching requests
- Retrieve all requests
- Update request status

---

## MatchingEngine

Handles the matching process.

Responsibilities:
- Run the matching algorithm
- Return matched candidates

---

## MatchingStrategy

Defines the matching strategy interface.

Responsibilities:
- Provide a common matching method
- Allow different matching strategies

---

## SkillMatchingStrategy

Matches teachers based on skills.

Responsibilities:
- Compare required skills with teacher skills
- Return suitable candidates

---

## TrainingManager

Handles training related operations.

Responsibilities:
- Record training information
- Update training records for teachers

---

# 3. Persistence Layer

The persistence layer handles data storage.

## SimpleDatabase

Acts as a simple database for the system.

Responsibilities:
- Store courses
- Store PTT members
- Store teaching requests
- Load data from file
- Save data to file

---

# 4. Controller Layer

Handles user interaction.

## Main

Main entry point of the program.

Responsibilities:
- Display CLI menu
- Read user input
- Call service functions

Main functions:
- create request
- match candidates
- record training
- save data
