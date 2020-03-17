package containers;

import agents.BuyerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BuyerContainer extends Application {


    protected BuyerAgent agent;
    ObservableList<String> observableList;

    public static void main(String[] args) {
        launch(args);
    }

    public void setAgent(BuyerAgent agent) {
        this.agent = agent;
    }

    public void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController agentController = container
                .createNewAgent("Buyer", "agents.BuyerAgent", new Object[]{this});

        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableList = FXCollections.observableArrayList();
        ListView<String> listViewMessages = new ListView<>(observableList);
        vBox.getChildren().add(listViewMessages);
        BorderPane pane = new BorderPane();
        pane.setCenter(vBox);

        Scene scene = new Scene(pane, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Buyer");

        stage.show();
    }

    public void logMessage(ACLMessage message) {
        Platform.runLater(() ->
                observableList.add(message.getContent() + ", " + message.getSender()));
    }
}
