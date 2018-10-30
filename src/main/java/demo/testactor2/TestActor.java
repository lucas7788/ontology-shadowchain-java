package demo.testactor2;

import java.util.HashMap;
import java.util.Map;

import com.ibm.actor.AbstractActor;
import com.ibm.actor.Actor;
import com.ibm.actor.DefaultActorManager;
import com.ibm.actor.DefaultMessage;
import com.ibm.actor.Message;

public class TestActor extends AbstractActor {

    private Actor to;

    public void setTestActor(Actor sendTo) {
        this.to = sendTo;
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void deactivate() {
        logger.trace("TestActor deactivate: %s", getName());
        super.deactivate();
    }

    @Override
    protected void runBody() {
        DefaultMessage m = new DefaultMessage("message->from :" + getName(), 20);
        getManager().send(m, null, this);
    }

    @Override
    protected void loopBody(Message m) {
        String subject = m.getSubject();
        int count = (Integer) m.getData();
        if (count > 0) {
            m = new DefaultMessage("message->from :" + getName(), count - 1);
            System.out.println(getName() + ":" + subject + " data:" + m.getData());
            getManager().send(m, this, to);
        }

    }

    public static void main(String[] args) {
        DefaultActorManager am = new DefaultActorManager();
        Map<String, Actor> actors = new HashMap<String, Actor>();
        try {
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(DefaultActorManager.ACTOR_THREAD_COUNT, 2);
            am.initialize(options);
            TestActor a = (TestActor) am.createActor(TestActor.class, "actor0");
            actors.put(a.getName(), a);
            TestActor b = (TestActor) am.createActor(TestActor.class, "actor1");
            actors.put(b.getName(), b);
            a.setTestActor(b);
            b.setTestActor(a);
            for (String key : actors.keySet()) {
                am.startActor(actors.get(key));
            }
            Thread.sleep(10000000);
            // am.terminateAndWait();
        } catch (Exception e) {
        }
    }
}
