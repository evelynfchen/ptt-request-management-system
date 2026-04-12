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