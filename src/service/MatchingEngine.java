package service;

import entity.PTTMember;
import entity.TeachingRequest;
import java.util.Objects;
import java.util.List;

public class MatchingEngine {
    private MatchingStrategy strategy;

    public MatchingEngine(MatchingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
    }

    public List<PTTMember> matchCandidates(TeachingRequest request, List<PTTMember> allStaff) {
        return strategy.match(request, allStaff);
    }
}
