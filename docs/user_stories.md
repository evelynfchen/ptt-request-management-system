us1
Title: Submit Request
User Story: As a Course Director, I want to submit a teaching request with course details and required skills, so that suitable PTT candidates can later be identified.
Acceptance Criteria: 
1.The system validates mandatory fields such as courseCode, requiredSkills, and budget.
2.A new TeachingRequest object is created with status PENDING.
3.The request is stored in the system for later candidate matching.
Priority: Must-have

us2
Title:Match PTT Candidates
User Story:As an Administrator, I want the system to find suitable PTT members based on the required skills in a teaching request, so that appropriate candidates can be identified efficiently.
Acceptance Criteria:
1.The system compares TeachingRequest.requiredSkills with PTTMember.skills.
2.Only PTT members with relevant matching skills are returned.
3.The matched candidate list is attached to the TeachingRequest.
4.The request status changes from PENDING to MATCHED when matching is completed.
Priority:Must-have

us3
Title:Record PTT Training
User Story:As an Administrator, I want to record training information for PTT members, so that their training status and history are available for future allocation.
Acceptance Criteria:
1.Training records include at least a topic and a date.
2.When training is recorded successfully, PTTMember.isTrained is updated to true.
3.Historical training records can be viewed later for each PTT member.
Priority:Could-have

us4
Title:System Data Persistence
User Story:As an Administrator, I want the system to load and save application data from a file, so that information is preserved across sessions without requiring a real database.
Acceptance Criteria:
1.A file such as data.txt is read when the program starts.
2.The system rebuilds in-memory objects from the stored file data.
3.All updates are written back to the file before the program exits.
4.The file format is validated to reduce the risk of corrupted or invalid data.
Priority:Must-have
