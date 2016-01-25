package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Gender;
import com.scholarscore.models.School;
import com.scholarscore.models.gpa.AddedValueGpa;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;
import com.scholarscore.models.message.topic.GpaTopic;
import com.scholarscore.models.message.topic.MessageTopic;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

/**
 * Created by markroper on 1/18/16.
 */
@Test(groups = { "integration" })
public class MessageControllerIntegrationTest extends IntegrationBase {
    private School school;
    private Student student1;
    private Student student2;
    private Student student3;
    private Student student4;
    private Staff teacher;

    private Gpa gpa1;

    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        teacher = new Staff();
        teacher.setIsTeacher(true);
        teacher.setName("Mr. Jones");
        teacher.setCurrentSchoolId(school.getId());
        teacher = teacherValidatingExecutor.create(teacher, "Create a base teacher");

        student1 = new Student();
        student1.setName(localeServiceUtil.generateName());
        student1.setCurrentSchoolId(school.getId());
        student1.setFederalEthnicity("true");
        student1.setGender(Gender.MALE);
        student1.setFederalRace("W");
        student1 = studentValidatingExecutor.create(student1, "create base student");
        gpa1 = new AddedValueGpa();
        gpa1.setCalculationDate(LocalDate.now());
        gpa1.setStudentId(student1.getId());
        gpa1.setScore(3.3);
        gpaValidatingExecutor.create(student1.getUserId(), gpa1, "GPA for student1");

        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2.setCurrentSchoolId(school.getId());
        student2.setFederalEthnicity("false");
        student2.setGender(Gender.FEMALE);
        student2.setFederalRace("A");
        student2 = studentValidatingExecutor.create(student2, "create base student");
        AddedValueGpa gpa2 = new AddedValueGpa();
        gpa2.setCalculationDate(LocalDate.now());
        gpa2.setStudentId(student2.getId());
        gpa2.setScore(2.8);
        gpaValidatingExecutor.create(student2.getUserId(), gpa2, "GPA for student1");

        student3 = new Student();
        student3.setName(localeServiceUtil.generateName());
        student3.setCurrentSchoolId(school.getId());
        student3.setFederalEthnicity("true");
        student3.setGender(Gender.MALE);
        student3.setFederalRace("B");
        student3 = studentValidatingExecutor.create(student3, "create base student");
        AddedValueGpa gpa3 = new AddedValueGpa();
        gpa3.setCalculationDate(LocalDate.now());
        gpa3.setStudentId(student3.getId());
        gpa3.setScore(2.9);
        gpaValidatingExecutor.create(student3.getUserId(), gpa3, "GPA for student1");

        student4 = new Student();
        student4.setName(localeServiceUtil.generateName());
        student4.setCurrentSchoolId(school.getId());
        student4.setFederalEthnicity("false");
        student4.setGender(Gender.FEMALE);
        student4.setFederalRace("I");
        student4 = studentValidatingExecutor.create(student4, "create base student");
        AddedValueGpa gpa4 = new AddedValueGpa();
        gpa4.setCalculationDate(LocalDate.now());
        gpa4.setStudentId(student4.getId());
        gpa4.setScore(3.8);
        gpaValidatingExecutor.create(student4.getUserId(), gpa4, "GPA for student1");
    }

    @DataProvider
    public Object[][] messageThreadProvider() {
        MessageThread emptyThread = new MessageThread();

        MessageThreadParticipant p1 = new MessageThreadParticipant();
        p1.setParticipantId(student1.getId());
        MessageThreadParticipant p2 = new MessageThreadParticipant();
        p2.setParticipantId(teacher.getId());
        MessageThread threadWithParticipants = new MessageThread();
        threadWithParticipants.setParticipants(new HashSet<>());
        threadWithParticipants.getParticipants().add(p1);
        threadWithParticipants.getParticipants().add(p2);

        return new Object[][]{
                {"Empty thread with no participants", emptyThread},
                {"Thread with two participants", threadWithParticipants}
        };
    }

    @Test(dataProvider = "messageThreadProvider")
    public void createThread(String msg, MessageThread t) {
        MessageThread created = messageValidatingExecutor.createThread(t, msg);
        messageValidatingExecutor.deleteThread(created.getId(), "deleting the thread");
    }

    @DataProvider
    public Object[][] messageProvider() {
        MessageThreadParticipant p1 = new MessageThreadParticipant();
        p1.setParticipantId(student1.getId());
        MessageThreadParticipant p2 = new MessageThreadParticipant();
        p2.setParticipantId(teacher.getId());
        MessageThreadParticipant p3 = new MessageThreadParticipant();
        p3.setParticipantId(student3.getId());
        MessageThread threadWithParticipants = new MessageThread();
        threadWithParticipants.setParticipants(new HashSet<>());
        threadWithParticipants.getParticipants().add(p1);
        threadWithParticipants.getParticipants().add(p2);
        threadWithParticipants.getParticipants().add(p3);
        MessageThread t = messageValidatingExecutor.createThread(threadWithParticipants, "Data provider");

        Message m1 = new Message();
        m1.setThread(t);
        m1.setBody("First message");
        m1.setSentBy(student1.getId());

        Message m2 = new Message();
        m2.setThread(t);
        m2.setBody("Second message");
        m2.setSentBy(teacher.getId());

        Message m3 = new Message();
        m3.setThread(t);
        m3.setBody("Third message");
        m3.setSentBy(student2.getId());

        return new Object[][]{
                { "First messgae", m1 },
                { "Second messgae", m2 },
                { "Third messgae", m3 }
        };
    }

    @Test(dataProvider = "messageProvider")
    public void createUpdateDeleteMessageTest(String msg, Message m) {
        Message created = messageValidatingExecutor.createMessage(
                m.getThread().getId(), m, "creating message to then update and delete it");
        messageValidatingExecutor.delete(m.getThread().getId(), m.getId(), "Deleting message");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAndRetrieveAndMarkAsRead() {
        Object[][] msgs = messageProvider();
        MessageThread t = ((Message)msgs[0][1]).getThread();
        for(int i = 0; i < msgs.length; i++) {
            Message m = (Message)msgs[i][1];
            messageValidatingExecutor.createMessage(t.getId(), m, (String)msgs[i][0]);
        }
        List<Message> messages = messageValidatingExecutor.getAllThreadMessages(t.getId(), "Get thread messages");
        Assert.assertTrue(messages.size() == 3, "Unexpected number of messages");

        for(Message mess: messages) {
            messageValidatingExecutor.markMessageReadForUser(
                    t.getId(), mess.getId(), student1.getId(), "Mark unread for student 1");
        }

        List<Message> stud1Messages = messageValidatingExecutor.getUnreadMessagesForUserOnThread(
                t.getId(), student1.getId(), "unread msgs for student 1");
        Assert.assertTrue(stud1Messages.size() == 0, "Unexpected unread messages returned for student 1");

        List<Message> teacherMessages = messageValidatingExecutor.getUnreadMessagesForUserOnThread(
                t.getId(), teacher.getId(), "unread msgs for teacher");
        Assert.assertTrue(teacherMessages.size() == 3, "Unexpected unread messages returned for teacher");
    }

    @Test
    public void createGpaMessage() {
        MessageTopic top = new GpaTopic();
        top.setSchoolId(school.getId());
        top.setFk(gpa1.getId());

        MessageThreadParticipant p1 = new MessageThreadParticipant();
        p1.setParticipantId(student1.getId());
        MessageThreadParticipant p2 = new MessageThreadParticipant();
        p2.setParticipantId(teacher.getId());
        MessageThread threadWithParticipants = new MessageThread();
        threadWithParticipants.setParticipants(new HashSet<>());
        threadWithParticipants.getParticipants().add(p1);
        threadWithParticipants.getParticipants().add(p2);
        threadWithParticipants.setTopic(top);
        MessageThread t = messageValidatingExecutor.createThread(threadWithParticipants, "Data provider");

    }
}
