package agents;

import containers.BuyerContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class BuyerAgent extends GuiAgent {

    protected transient BuyerContainer container;
    private AID[] sellerAgents;

    @Override
    protected void setup() {
        System.out.println("Initializing agent :" + this.getAID().getName());
        if (getArguments().length == 1) {
            container = (BuyerContainer) getArguments()[0];
            container.setAgent(this);
        }
        System.out.println("args :" + this.getArguments()[0]);
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setName("book-selling");
                template.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent, template);
                    sellerAgents = new AID[results.length];
                    System.out.println(results.length);
                    for (int i = 0; i < results.length; i++) {
                        sellerAgents[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private List<ACLMessage> replies = new ArrayList<ACLMessage>();
            private int counter;

            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    switch (message.getPerformative()) {
                        case ACLMessage.REQUEST:
                            ACLMessage message1 = new ACLMessage(ACLMessage.CFP);
                            message1.setContent(message.getContent());
                            for (AID vendor : sellerAgents)
                                message1.addReceiver(vendor);
                            send(message1);
                            break;
                        case ACLMessage.PROPOSE:
                            ++counter;
                            replies.add(message);
                            if (counter == replies.size()) {
                                ACLMessage bestOffer = replies.get(0);
                                double minimal = Double.parseDouble(bestOffer.getContent());
                                for (ACLMessage offer : replies) {
                                    double price = Double.parseDouble(offer.getContent());
                                    if (minimal > price) {
                                        bestOffer = offer;
                                        minimal = price;
                                    }
                                }
                                ACLMessage aclMessageAccept = bestOffer.createReply();
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);
                            }
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            aclMessage.setContent(message.getContent());
                            send(aclMessage);
                        case ACLMessage.REFUSE:
                        default:
                            break;
                    }

                    String book = message.getContent();
                    container.logMessage(message);
                    ACLMessage response = message.createReply();
                    response.setContent(book + " added !");
                    send(response);
//                    ACLMessage request = new ACLMessage(ACLMessage.CFP);
//                    request.setContent(book);
//                    request.addReceiver(new AID("Vendor", AID.ISLOCALNAME));
//                    send(request);
                } else block();
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    public void onGuiEvent(GuiEvent params) {
    }
}
