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
