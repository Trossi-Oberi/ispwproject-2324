import logic.beans.BEvent;
import logic.controllers.CFacade;
import logic.exceptions.EventAlreadyAdded;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*Nicolas Oberi*/


public class TestManageEvent {
    //test login, addevent, participate, chat (last message), joingroup, notification

    //TODO: fare 3 test per persona

    CFacade facade;
    LoggedUser logUser;
    private static final String MESSAGE = "Hi! How are you?";

    public TestManageEvent() {
        facade = new CFacade();
    }

    @Test
    public void addEvent() {
//        String s = "Hello World";
//        assertEquals("Hello World",s);
        LoggedUser.setUserName("Organizer-Test");
        LoggedUser.setUserID(10);
        BEvent eventBean = new BEvent();
        try {
            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
            eventBean.setEventName("test-event");
            eventBean.setEventProvince("Roma");
            eventBean.setEventCity("Roma");
            eventBean.setEventAddress("Via Milano 5");
            eventBean.setEventMusicGenre("Pop");
            eventBean.setEventDate("20-05-2024");
            eventBean.setEventTime("22", "22");
            eventBean.setEventPicPath("dummy-path");
            facade.addEvent(eventBean);

        } catch (InvalidValueException | TextTooLongException | EventAlreadyAdded e) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, e::getMessage);
        }

        List<BEvent> myEvents = facade.retrieveEvents(UserTypes.ORGANIZER, "GCYourEventsOrg");
        if (myEvents.isEmpty()) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "No events found associated to you");
        } else {
            for (BEvent event : myEvents) {
                assertEquals(eventBean.getEventName(), event.getEventName());
            }
        }
    }

    @Test
    public void editEvent() {
        LoggedUser.setUserID(2);
        List<BEvent> myEvents = facade.retrieveEvents(UserTypes.ORGANIZER, "GCYourEventsOrg");
        if (myEvents.isEmpty()) {
            Logger.getLogger("NightPlan").log(Level.SEVERE, "No events to be shown");
        } else {
            BEvent exampleEvent = myEvents.getFirst();
            try {
                exampleEvent.setEventName("Edited-name-test");
            } catch (InvalidValueException | TextTooLongException e) {
                Logger.getLogger("NightPlan").log(Level.SEVERE, e.getMessage());
            }
            facade.editEvent(exampleEvent);
            myEvents = facade.retrieveEvents(UserTypes.ORGANIZER, "GCYourEventsOrg");
            assertEquals(exampleEvent.getEventName(), myEvents.getFirst().getEventName());
        }
    }

}

