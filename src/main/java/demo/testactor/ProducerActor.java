package demo.testactor;

import com.ibm.actor.AbstractActor;
import com.ibm.actor.Actor;
import com.ibm.actor.DefaultMessage;
import com.ibm.actor.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProducerActor extends AbstractActor {
    Map<String , Integer> expected = new ConcurrentHashMap<String, Integer>();

    @Override
    protected void loopBody(Message m) {
        String subject = m.getSubject();
        if ("produceN".equals(subject)) {
            Object[] input = (Object[]) m.getData();
            int count = (Integer) input[0];
            if (count > 0) {
//                DefaultActorTest.sleeper(1); // this takes some time
                String type = (String) input[1];
                // request the consumers to consume work (i.e., produce)
                Integer mcount = expected.get(type);
                if (mcount == null) {
                    mcount = new Integer(0);
                }
                mcount += count;
                expected.put(type, mcount);

                DefaultMessage dm = new DefaultMessage("produce1",
                        new Object[]{count, type});
                getManager().send(dm, this, this);
            }
        } else if ("produce1".equals(subject)) {
            Object[] input = (Object[]) m.getData();
            int count = (Integer) input[0];
            if (count > 0) {
                sleep(100); // take a little time
                String type = (String) input[1];
                m = new DefaultMessage("construct", type);
                getManager().send(m, this, getConsumerCategory());

                m = new DefaultMessage("produce1", new Object[]{count - 1, type});
                getManager().send(m, this, this);
            }
        } else if ("init".equals(subject)) {
            // create some consumers; 1 to 3 x consumers per producer
            for (int i = 0; i < 3 + 1; i++) {
                Actor a = getManager().createAndStartActor(ConsumerActor.class,
                        String.format("%s_consumer%02d", getName(), i));
                a.setCategory(getConsumerCategory());
            }
        }else {
            System.out.printf("ProducerActor:%s loopBody unknown subject: %s%n",
                    getName(), subject);
        }
    }

    protected String getConsumerCategory() {
        return getName() + "_consumer";
    }
}
