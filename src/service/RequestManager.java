package service;

import entity.TeachingRequest;
import entity.RequestStatus;
import persistence.SimpleDatabase;
import java.util.ArrayList;
import java.util.List;

public class RequestManager {
    public void createRequest(TeachingRequest request) {
        if (request == null) {
            return;
        }

        SimpleDatabase database = SimpleDatabase.getInstance();
        List<TeachingRequest> requests = database.getRequests();
        if (requests == null) {
            requests = new ArrayList<>();
            database.setRequests(requests);
        }

        requests.add(request);
    }

    public void approveRequest(TeachingRequest request) {
        if (request != null) {
            request.setStatus(RequestStatus.MATCHED);
        }
    }

    public List<TeachingRequest> getAllRequests() {
        List<TeachingRequest> requests = SimpleDatabase.getInstance().getRequests();
        return requests != null ? requests : new ArrayList<>();
    }
}
