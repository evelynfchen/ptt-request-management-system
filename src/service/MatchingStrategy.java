package service;

import entity.PTTMember;
import entity.TeachingRequest;
import java.util.List;

public interface MatchingStrategy {
    List<PTTMember> match(TeachingRequest request, List<PTTMember> allStaff);
}
