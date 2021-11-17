package com.foxminded.javaee.university.service.data;

import com.foxminded.javaee.university.model.*;
import com.foxminded.javaee.university.service.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.concurrent.ThreadLocalRandom.current;

public class DataGenerator {
    private final GroupService groupService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassroomService classroomService;
    private final ScheduleService scheduleService;

    public DataGenerator(GroupService groupService, SubjectService subjectService,
                         StudentService studentService, TeacherService teacherService,
                         ClassroomService classroomService, ScheduleService scheduleService) {
        this.groupService = groupService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.classroomService = classroomService;
        this.scheduleService = scheduleService;
    }

    public void generateGroups(int num) {
        Set<String> groupNames = new HashSet<>();
        Random r = new Random();

        while(groupNames.size() < num) {
            groupNames.add(getRandomChar() + getRandomChar() + "-" + r.nextInt(100));
        }

        groupNames.forEach(s -> groupService.save(new Group(s)));
    }

    public void generateClassrooms(int num) {
        IntStream.range(1, num+1)
                .forEach(i -> classroomService.save(new Classroom(i + 10 + " auditory",
                        current().nextInt(18, 32)))
                );
    }

    public void generateStudents(int num) {
        List<String> firstNames = readFilePerLine("/first_names.txt");
        List<String> lastNames = readFilePerLine("/last_names.txt");
        Set<String> students = new HashSet<>();
        Random random = new Random();

        while (students.size()<num) {
            students.add(String.format("%s %s", firstNames.get(random.nextInt(firstNames.size())),
                    lastNames.get(random.nextInt(lastNames.size()))));
        }

        students.stream()
                .map(s -> s.split(" "))
                .forEach(split -> studentService.save(new Student(split[0], split[1],
                        current().nextLong(1, groupService.findAll().size()+1)
                )));
    }

    public void generateTeachers(int num) {
        List<String> firstNames = readFilePerLine("/first_names.txt");
        List<String> lastNames = readFilePerLine("/last_names.txt");
        Set<String> teachers = new HashSet<>();
        Random random = new Random();

        while (teachers.size()<num) {
            teachers.add(String.format("%s %s", firstNames.get(random.nextInt(firstNames.size())),
                    lastNames.get(random.nextInt(lastNames.size()))));
        }

        teachers.stream()
                .map(s -> s.split(" "))
                .forEach(split -> teacherService.save(
                        new Teacher(split[0], split[1],
                        Degree.of(
                                Degree.values()[random.nextInt(Degree.values().length)].toString()
                        ).getDescription()))
                );
    }

    public void generateSubjects() {
        for (String s : readFilePerLine("/subjects.txt")) {
            String[] split = s.split("_");
            subjectService.save(new Subject(split[0], split[1]));
        }
    }

    public void referenceGroupSubjects() {
        List<Subject> subjectList = new ArrayList<>();
        List<Group> groupList = new ArrayList<>();

        Random random = new Random();

        subjectList.addAll(subjectService.findAll());

        groupList.addAll(groupService.findAll());

        for (Group group : groupList) {
            for (int j = 0; j < 3; j++) {
                groupService.addSubjectToGroup(
                        subjectList.get(random.nextInt(subjectList.size())).getId(),
                        group.getId()
                );
            }
        }
    }

    public void generateSchedule(String from_date, int for_month_count) {
        LocalDate dt = LocalDate.parse(from_date);

        for (int i=0; i<for_month_count; i++) {
            for (int j = 0; j < dt.lengthOfMonth()-1; j++) {
                if (dt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    dt = dt.plusDays(1);
                    continue;
                }

                long curGroup_id = current().nextLong(1, groupService.findAll().size()+1);

                for (int k = 0; k < 3; k++) { // subject count

                    long curSubject_id = current().nextLong(1,
                            subjectService.findAllSubjectsInGroup(curGroup_id).size()+1);
                    long curTeacher_id = current().nextLong(1, teacherService.findAll().size()+1);
                    long curClassroom_id = current().nextLong(1, classroomService.findAll().size()+1);

                    scheduleService.save(new Schedule(
                            Date.valueOf(dt),
                            curGroup_id,
                            curTeacher_id,
                            LectureTime.of(LectureTime.values()[k].toString()).getTime(),
                            curSubject_id,
                            curClassroom_id)
                    );
                }
                dt = dt.plusDays(1);
            }
        }
    }

    private String getRandomChar() {
        Random r = new Random();
        return String.valueOf((char)(r.nextInt(26) + 'a'));
    }

    private List<String> readFilePerLine(String fileName) {
        InputStream is = null;
        BufferedReader br = null;
        String line;
        ArrayList<String> list = new ArrayList<>();

        try {
            is = FileUtils.class.getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine())) {
                list.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (br != null) br.close();
                if (is != null) is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
