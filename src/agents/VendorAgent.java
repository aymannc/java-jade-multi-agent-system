package agents;

import containers.VendorContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendorAgent extends GuiAgent {

    protected transient VendorContainer container;

    @Override
    protected void setup() {
        System.out.println("Initializing agent :" + this.getAID().getName());
        if (getArguments().length == 1) {
            container = (VendorContainer) getArguments()[0];
            container.setAgent(this);
        }
        System.out.println("args :" + this.getArguments()[0]);
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transaction");
                serviceDescription.setName("book-selling");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }

        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    container.logMessage(message);
                    switch (message.getPerformative()) {
                        case ACLMessage.CFP:
                            ACLMessage replyMessage = message.createReply();
                            replyMessage.setPerformative(ACLMessage.PROPOSE);
                            replyMessage.setContent(String.valueOf(500+new Random().nextInt(1000)));
                            send(replyMessage);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage aclMessage = message.createReply();
                            aclMessage.setPerformative(ACLMessage.PROPOSE);
                            send(aclMessage);
                            break;
                        default:
                            break;
                    }
                } else block();
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    public void onGuiEvent(GuiEvent params) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
