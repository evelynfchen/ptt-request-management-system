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
