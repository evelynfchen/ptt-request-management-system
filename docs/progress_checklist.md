Progress Checklist

This checklist maps the sequence diagrams to the implemented methods in the system.
It helps verify that each user scenario is supported by the current implementation.

1. Submit Request (Input Course Requirements)

Sequence diagram: Input Course Requirements

Functions required by the scenario:
•	Enter course code
•	Enter required skills
•	Create a teaching request
•	Store request information
•	Confirm request creation

Related methods in the system:
•	createRequest() (Main / UI)
•	RequestManager.submitRequest()
•	TeachingRequest() constructor
•	TeachingRequest.addRequiredSkill()
•	SimpleDatabase.saveData()

Status: Implemented

2. Match Candidates

Sequence diagram: Search Qualified Staff

Functions required by the scenario:
•	Select course request
•	Retrieve required skills
•	Search staff records
•	Perform skill matching
•	Return matched candidate list

Related methods in the system:
•	matchCandidates() (Main / UI)
•	MatchingEngine.findMatches()
•	SkillMatchingStrategy.match()
•	PTTMember.getSkills()
•	TeachingRequest.setMatchedCandidates()
•	TeachingRequest.setStatus(RequestStatus.MATCHED)

Status: Implemented

3. Record Training

Sequence diagram: Arrange Training Tasks / Update Staff Skills

Functions required by the scenario:
•	Select staff member for training
•	Create training record
•	Update staff skills
•	Update training status

Related methods in the system:
•	TrainingManager.recordTraining()
•	PTTMember.addSkill()
•	PTTMember.addTrainingRecord()
•	TrainingManager.getTrainingHistory()

Status: Implemented

4. Save / Load System Data

Sequence diagram: System Exit / Restart

Functions required by the scenario:
•	Save system data before exiting
•	Load stored data when the system starts
•	Ensure persistence of requests, staff, and training records

Related methods in the system:
•	SimpleDatabase.saveData()
•	SimpleDatabase.loadData()

Status: Implemented

Summary

All four main scenarios from the sequence diagrams are supported by the current system implementation.

The system structure follows a layered architecture:
•	Entity layer
•	Service layer
•	Persistence layer

The staff matching logic is implemented using the Strategy Pattern, while data storage is handled through a single persistence class (SimpleDatabase).