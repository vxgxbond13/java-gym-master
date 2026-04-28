package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TimetableTest {

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());//Проверить, что за понедельник вернулось одно занятие
        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY).size());//Проверить, что за вторник не вернулось занятий
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        // Проверить, что за понедельник вернулось одно занятие
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
        // Проверить, что за четверг вернулось два занятия в правильном порядке: сначала в 13:00, потом в 20:00
        assertEquals(2, timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY).size());
        List<TimeOfDay> times = new ArrayList<>(timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY).keySet());
        assertEquals(13, times.get(0).getHours());
        assertEquals(0, times.get(0).getMinutes());
        assertEquals(20, times.get(1).getHours());
        assertEquals(0, times.get(1).getMinutes());
        // Проверить, что за вторник не вернулось занятий
        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY).size());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник в 13:00 вернулось одно занятие
        List<TrainingSession> sessionsAt13 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        assertEquals(1, sessionsAt13.size());
        //Проверить, что за понедельник в 14:00 не вернулось занятий
        List<TrainingSession> sessionsAt14 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        assertNull(sessionsAt14);
    }

    //Несколько тренировок в одно и то же время
    @Test
    void testMultipleSessionsAtSameTime() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Иванов", "Иван", "Иванович");
        Group group1 = new Group("Йога", Age.ADULT, 60);
        Group group2 = new Group("Пилатес", Age.ADULT, 45);

        TrainingSession session1 = new TrainingSession(group1, coach,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        TrainingSession session2 = new TrainingSession(group2, coach,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(session1);
        timetable.addNewTrainingSession(session2);

        List<TrainingSession> sessions = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(10, 0));

        assertEquals(2, sessions.size());
    }

    //Пустое расписание
    @Test
    void testEmptyTimetable() {
        Timetable timetable = new Timetable();

        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
    }

    //Граничные значения
    @Test
    void testBoundaryTimes() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Тест", "Тест", "Тестович");
        Group group = new Group("Зарядка", Age.ADULT, 30);

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(0, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(23, 59)));

        Map<TimeOfDay, List<TrainingSession>> sessions =
                timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);

        assertEquals(2, sessions.size());
        assertTrue(sessions.containsKey(new TimeOfDay(0, 0)));
        assertTrue(sessions.containsKey(new TimeOfDay(23, 59)));
    }

    @Test
    void testCountByCoachesSingleCoachOneSession() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Иванов", "Иван", "Иванович");
        Group group = new Group("Йога", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0)));

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(1, result.size());
        assertEquals(coach, result.get(0).getCoach());
        assertEquals(1, result.get(0).getCount());
    }

    @Test
    void testCountByCoachesSingleCoachMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Петров", "Пётр", "Петрович");
        Group group = new Group("Фитнес", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.FRIDAY, new TimeOfDay(15, 0)));

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getCount());
    }

    @Test
    void testCountByCoachesTwoCoaches() {
        Timetable timetable = new Timetable();

        Coach coach1 = new Coach("Сидоров", "Сидор", "Сидорович");
        Coach coach2 = new Coach("Кузнецов", "Кузнец", "Кузнецович");
        Group group = new Group("Зарядка", Age.ADULT, 30);

        // coach1 — 2 тренировки
        timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0)));

        // coach2 — 1 тренировка
        timetable.addNewTrainingSession(new TrainingSession(group, coach2,
                DayOfWeek.FRIDAY, new TimeOfDay(10, 0)));

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getCount()); // coach1 должен быть первым (больше тренировок)
        assertEquals(1, result.get(1).getCount());
    }
}
