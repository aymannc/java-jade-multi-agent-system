package containers;

import agents.ConsumerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {
    protected ConsumerAgent agent;
    ObservableList<String> observableList;

    public static void main(String[] args) {
        launch(args);
    }

    public void setAgent(ConsumerAgent agent) {
        this.agent = agent;
    }

    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController agentController = container
                .createNewAgent("Consumer", "agents.ConsumerAgent", new Object[]{this});

        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10));
        hbox.setSpacing(10);
        Label label = new Label("Book");
        TextField textField = new TextField();
        Button button = new Button("Buy");
        hbox.getChildren().addAll(label, textField, button);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableList = FXCollections.observableArrayList();
        ListView<String> listViewMessages = new ListView<>(observableList);
        vBox.getChildren().add(listViewMessages);
        BorderPane pane = new BorderPane();
        pane.setTop(hbox);
        pane.setCenter(vBox);

        Scene scene = new Scene(pane, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Consumer");
        stage.show();


        button.setOnAction(e -> {
            String livre = textField.getText();
            GuiEvent event = new GuiEvent(this, 1);
            event.addParameter(livre);
            agent.onGuiEvent(event);
        });
    }

    public void logMessage(ACLMessage message) {
        Platform.runLater(() ->
                observableList.add(message.getContent() + ", " + message.getSender()));
    }
}
