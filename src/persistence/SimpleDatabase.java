package persistence;

import entity.Course;
import entity.PTTMember;
import entity.RequestStatus;
import entity.TeachingRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleDatabase {

    private static final String DATA_FILE = "data/data.txt";

    private static SimpleDatabase instance;

    private List<Course> courses;
    private List<PTTMember> pttMembers;
    private List<TeachingRequest> requests;

    private SimpleDatabase() {
        this.courses = new ArrayList<>();
        this.pttMembers = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public static synchronized SimpleDatabase getInstance() {
        if (instance == null) {
            instance = new SimpleDatabase();
        }
        return instance;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<PTTMember> getPttMembers() {
        return pttMembers;
    }

    public void setPttMembers(List<PTTMember> pttMembers) {
        this.pttMembers = pttMembers;
    }

    public List<TeachingRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<TeachingRequest> requests) {
        this.requests = requests;
    }

    public void loadData() {
        ensureDataFileExists();

        courses.clear();
        pttMembers.clear();
        requests.clear();

        Section currentSection = Section.NONE;

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String rawLine;
            while ((rawLine = br.readLine()) != null) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                Section section = Section.fromHeader(line);
                if (section != null) {
                    currentSection = section;
                    continue;
                }

                try {
                    switch (currentSection) {
                        case COURSES:
                            Course course = parseCourseLine(line);
                            if (course != null) {
                                addOrUpdateCourse(course);
                            }
                            break;
                        case PTT_MEMBERS:
                            PTTMember member = parseMemberLine(line);
                            if (member != null) {
                                pttMembers.add(member);
                            }
                            break;
                        case REQUESTS:
                            TeachingRequest request = parseRequestLine(line);
                            if (request != null) {
                                requests.add(request);
                            }
                            break;
                        case NONE:
                        default:
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    public void saveData() {
        ensureDataFileExists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            bw.write("[COURSES]");
            bw.newLine();
            for (Course course : courses) {
                bw.write(serializeCourse(course));
                bw.newLine();
            }
            bw.newLine();

            bw.write("[PTT_MEMBERS]");
            bw.newLine();
            for (PTTMember member : pttMembers) {
                bw.write(serializeMember(member));
                bw.newLine();
            }
            bw.newLine();

            bw.write("[REQUESTS]");
            bw.newLine();
            for (TeachingRequest request : requests) {
                bw.write(serializeRequest(request));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    public Course findCourseByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Course course : courses) {
            if (code.equals(course.getCourseCode())) {
                return course;
            }
        }
        return null;
    }

    public PTTMember findMemberById(String id) {
        if (id == null) {
            return null;
        }
        for (PTTMember member : pttMembers) {
            if (id.equals(member.getGuid())) {
                return member;
            }
        }
        return null;
    }

    public TeachingRequest findRequestByCourse(String courseCode) {
        if (courseCode == null) {
            return null;
        }
        for (TeachingRequest request : requests) {
            Course course = request.getCourse();
            if (course != null && courseCode.equals(course.getCourseCode())) {
                return request;
            }
        }
        return null;
    }

    public void addCourse(Course course) {
        if (course != null) {
            courses.add(course);
        }
    }

    public void addPTTMember(PTTMember member) {
        if (member != null) {
            pttMembers.add(member);
        }
    }

    public void addRequest(TeachingRequest request) {
        if (request != null) {
            requests.add(request);
        }
    }

    private void ensureDataFileExists() {
        File file = new File(DATA_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                System.out.println("Failed to create data directory: " + parent.getPath());
            }
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Failed to create data file: " + file.getPath());
                }
            } catch (IOException e) {
                System.out.println("Failed to create data file: " + e.getMessage());
            }
        }
    }

    private Course parseCourseLine(String line) {
        String[] parts = splitLine(line, 2);
        if (parts == null) {
            return null;
        }
        String code = parts[0].trim();
        String budgetText = parts[1].trim();
        if (code.isEmpty()) {
            return null;
        }
        try {
            double budget = Double.parseDouble(budgetText);
            return new Course(code, budget);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private PTTMember parseMemberLine(String line) {
        String[] parts = splitLine(line, 4);
        if (parts == null) {
            return null;
        }
        String guid = parts[0].trim();
        String name = parts[1].trim();
        String skillsText = parts[2].trim();
        String availableText = parts[3].trim();

        if (guid.isEmpty() || name.isEmpty()) {
            return null;
        }

        List<String> skills = splitCsv(skillsText);
        Boolean available = parseBooleanStrict(availableText);
        if (available == null) {
            return null;
        }

        PTTMember member = new PTTMember(guid, name, skills);
        member.setTrained(available);
        return member;
    }

    private TeachingRequest parseRequestLine(String line) {
        String[] parts = splitLine(line, 4);
        if (parts == null) {
            return null;
        }
        String courseCode = parts[0].trim();
        String skillsText = parts[1].trim();
        String statusText = parts[2].trim();
        String matchedMemberId = parts[3].trim();

        if (courseCode.isEmpty()) {
            return null;
        }

        Course course = findCourseByCode(courseCode);
        if (course == null) {
            course = new Course(courseCode, 0);
            courses.add(course);
        }

        List<String> requiredSkills = splitCsv(skillsText);
        TeachingRequest request = new TeachingRequest(course, requiredSkills);
        request.setStatus(parseStatus(statusText));

        if (!matchedMemberId.isEmpty()) {
            PTTMember member = findMemberById(matchedMemberId);
            if (member != null) {
                request.addMatchedCandidate(member);
            }
        }

        return request;
    }

    private void addOrUpdateCourse(Course course) {
        Course existing = findCourseByCode(course.getCourseCode());
        if (existing == null) {
            courses.add(course);
        } else {
            existing.setBudget(course.getBudget());
        }
    }

    private String serializeCourse(Course course) {
        if (course == null) {
            return "";
        }
        return course.getCourseCode() + "|" + course.getBudget();
    }

    private String serializeMember(PTTMember member) {
        if (member == null) {
            return "";
        }
        String skillsText = String.join(",", safeSkills(member.getSkills()));
        return member.getGuid() + "|" + member.getName() + "|" + skillsText + "|" + member.isTrained();
    }

    private String serializeRequest(TeachingRequest request) {
        if (request == null) {
            return "";
        }
        String courseCode = request.getCourse() == null ? "" : request.getCourse().getCourseCode();
        String skillsText = String.join(",", safeSkills(request.getRequiredSkills()));
        String statusText = request.getStatus() == null ? RequestStatus.PENDING.name() : request.getStatus().name();
        String matchedMemberId = "";
        List<PTTMember> matched = request.getMatchedCandidates();
        if (matched != null && !matched.isEmpty() && matched.get(0) != null) {
            matchedMemberId = matched.get(0).getGuid();
        }
        return courseCode + "|" + skillsText + "|" + statusText + "|" + matchedMemberId;
    }

    private String[] splitLine(String line, int expectedParts) {
        if (line == null) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length != expectedParts) {
            return null;
        }
        return parts;
    }

    private List<String> splitCsv(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = text.split(",");
        List<String> results = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                results.add(trimmed);
            }
        }
        return results;
    }

    private List<String> safeSkills(List<String> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills;
    }

    private RequestStatus parseStatus(String text) {
        if (text == null || text.trim().isEmpty()) {
            return RequestStatus.PENDING;
        }
        try {
            return RequestStatus.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
        }
    }

    private Boolean parseBooleanStrict(String text) {
        if (text == null) {
            return null;
        }
        String normalized = text.trim().toLowerCase();
        if ("true".equals(normalized)) {
            return Boolean.TRUE;
        }
        if ("false".equals(normalized)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private enum Section {
        NONE,
        COURSES,
        PTT_MEMBERS,
        REQUESTS;

        private static Section fromHeader(String line) {
            if ("[COURSES]".equalsIgnoreCase(line)) {
                return COURSES;
            }
            if ("[PTT_MEMBERS]".equalsIgnoreCase(line)) {
                return PTT_MEMBERS;
            }
            if ("[REQUESTS]".equalsIgnoreCase(line)) {
                return REQUESTS;
            }
            return null;
        }
    }
}
