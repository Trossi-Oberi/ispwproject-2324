import logic.beans.BMessage;
import logic.controllers.CFacade;
import logic.utils.LoggedUser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*Nicolas Oberi*/

class TestGroupChat {
    CFacade facade;
    private static final String MESSAGE = "This is a test message";
    private static final Integer GROUP_ID = 2;

    public TestGroupChat() {
        facade = new CFacade();
    }

    @Test
    void sendMessage() {
        LoggedUser.setUserName("User1");
        LoggedUser.setUserID(3);

        BMessage messageBean = new BMessage(LoggedUser.getUserID(), MESSAGE, GROUP_ID);
        facade.sendMessageToGroup(messageBean);
        List<BMessage> messages = facade.retrieveGroupChat(GROUP_ID);
        assertEquals(messages.getLast().getMessage(), MESSAGE);

    }
}
