import entity.Course;
import entity.PTTMember;
import entity.TeachingRequest;
import entity.RequestStatus;
import persistence.SimpleDatabase;
import service.*;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final RequestManager requestManager = new RequestManager();
    private static final TrainingManager trainingManager = new TrainingManager();

    private static final MatchingStrategy strategy = new SkillMatchingStrategy();
    private static final MatchingEngine matchingEngine = new MatchingEngine(strategy);

    private static final SimpleDatabase db = SimpleDatabase.getInstance();

    public static void main(String[] args) {

        db.loadData();

        System.out.println("===== SEIT AE2 Part Time Teacher System =====");

        while (true) {

            printMenu();

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    createRequest();
                    break;

                case "2":
                    matchCandidates();
                    break;

                case "3":
                    recordTraining();
                    break;

                case "4":
                    saveData();
                    break;

                case "5":
                    db.saveData();
                    System.out.println("System exit.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void printMenu() {

        System.out.println("\n----------------------");
        System.out.println("1. Create Request");
        System.out.println("2. Match Candidates");
        System.out.println("3. Record Training");
        System.out.println("4. Save Data");
        System.out.println("5. Exit");
        System.out.println("----------------------");
        System.out.print("Choose option: ");
    }


    private static void createRequest() {

        System.out.print("Course code: ");
        String courseCode = scanner.nextLine();

        System.out.print("Course budget: ");
        String budgetInput = scanner.nextLine();
        double budget = 0;
        try {
            budget = Double.parseDouble(budgetInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid budget, defaulting to 0.");
        }
        Course course = new Course(courseCode, budget);

        System.out.print("Required skills (comma separated): ");
        String skillLine = scanner.nextLine();

        List<String> skills = new ArrayList<>();

        if (!skillLine.isEmpty()) {
            String[] arr = skillLine.split(",");
            for (String s : arr) {
                skills.add(s.trim());
            }
        }

        TeachingRequest request = new TeachingRequest(course, skills);

        requestManager.createRequest(request);

        System.out.println("Request created.");
    }

    private static void matchCandidates() {

        List<TeachingRequest> requests = requestManager.getAllRequests();
        List<PTTMember> staff = db.getPttMembers();

        if (requests == null || requests.isEmpty()) {
            System.out.println("No teaching requests found.");
            return;
        }

        if (staff == null || staff.isEmpty()) {
            System.out.println("No staff available.");
            return;
        }

        for (TeachingRequest request : requests) {

            List<PTTMember> matches =
                    matchingEngine.matchCandidates(request, staff);

            request.setMatchedCandidates(matches);
            if (!matches.isEmpty()) {
                request.setStatus(RequestStatus.MATCHED);
            }

            System.out.println("\nMatched candidates for course: " + request.getCourse().getCourseCode());

            if (matches.isEmpty()) {
                System.out.println("No match found.");
            } else {
                for (PTTMember m : matches) {
                    System.out.println("- " + m.getName());
                }
            }
        }
    }


    private static void recordTraining() {

        System.out.print("PTT Member ID: ");
        String id = scanner.nextLine();

        System.out.print("Training course: ");
        String trainingTopic = scanner.nextLine();

        PTTMember member = db.findMemberById(id);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        trainingManager.recordTraining(member, trainingTopic);

        System.out.println("Training recorded.");
    }


    private static void saveData() {

        db.saveData();

        System.out.println("Data saved.");
    }
}
