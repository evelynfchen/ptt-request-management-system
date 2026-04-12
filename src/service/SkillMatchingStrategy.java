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
