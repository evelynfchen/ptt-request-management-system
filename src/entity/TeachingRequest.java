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