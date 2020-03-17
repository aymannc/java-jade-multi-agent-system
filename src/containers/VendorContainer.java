package containers;

import agents.VendorAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendorContainer extends Application {

    protected VendorAgent agent;
    ObservableList<String> observableList;
    private AgentContainer container;

    public static void main(String[] args) {
        launch(args);
    }

    public void setAgent(VendorAgent agent) {
        this.agent = agent;
    }

    public void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        container = runtime.createAgentContainer(profile);
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

        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setSpacing(10);
        TextField agentNameField = new TextField();
        Button deployButton = new Button("Deploy");
        deployButton.setOnAction(e -> {
            try {
                String name = agentNameField.getText();
                container.createNewAgent(name, "agents.VendorAgent", new Object[]{this}).start();
            } catch (StaleProxyException ex) {
                ex.printStackTrace();
            }
        });
        header.getChildren().addAll(agentNameField, deployButton);
        pane.setTop(header);

        Scene scene = new Scene(pane, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Vendor");
        stage.show();
    }

    public void logMessage(ACLMessage message) {
        Platform.runLater(() ->
                observableList.add(message.getContent() + ", " + message.getSender()));
    }
}
