package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
    protected transient ConsumerContainer container;

    @Override
    protected void setup() {
        System.out.println("Initializing agent :" + this.getAID().getName());
        if (getArguments().length == 1) {
            container = (ConsumerContainer) getArguments()[0];
            container.setAgent(this);
        }
        System.out.println("args :" + this.getArguments()[0]);
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    switch (message.getPerformative()){
                        case ACLMessage.CONFIRM:
                            container.logMessage(message);
                            break;
                    }
                } else block();
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    public void onGuiEvent(GuiEvent params) {
        if (params.getType() == 1) {
            String livre = params.getParameter(0).toString();
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(livre);
            message.addReceiver(new AID("Buyer", AID.ISLOCALNAME));
            send(message);
        }
    }
}
