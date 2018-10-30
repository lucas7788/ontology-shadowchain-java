package demo.testactor;

import com.ibm.actor.AbstractActor;
import com.ibm.actor.DefaultMessage;
import com.ibm.actor.Message;

public class ConsumerActor extends AbstractActor {
    @Override
    protected void loopBody(Message m) {

        String subject = m.getSubject();
        if ("construct".equals(subject)) {
            String type = (String) m.getData();
            delay(type); // takes ~ 1 to N seconds

            DefaultMessage dm = new
                    DefaultMessage("constructionComplete", type);
            getManager().send(dm, this, m.getSource());
        } else if ("init".equals(subject)) {
            // nothing to do
        } else {
            System.out.printf("ConsumerActor:%s loopBody unknown subject: %s%n",
                    getName(), subject);
        }

    }
    protected void delay(String type) {
        int delay = 1;
        for (int i = 0; i < 4; i++) {
            delay++;
        }
    }
}
