package com.metait.javafxlgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import java.util.Optional;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import javafx.scene.web.WebView;
import java.net.URL;

public class LGameApplication extends Application {
    private LGameModel model;
    private LGameBoard board;
    private boolean gameOverAlertShown = false;

    private Label titleLabel;
    private Label statusLabel;
    private Label errorLabel;
    private Button btnRotate;
    private Button btnFlip;
    private Button btnConfirm;
    private Button btnSkip;
    private Button btnReset;

    private Menu gameMenu;
    private Menu helpMenu;
    private MenuItem itemSwitchUnfinished;
    private MenuItem itemEditNames;
    private MenuItem itemFinishedList;
    private MenuItem itemChooseLanguage;
    private MenuItem itemQuit;
    private MenuItem itemAbout;
    private MenuItem itemHelp;

    @Override
    public void start(Stage stage) {
        model = new LGameModel();
        model.loadState(); // Restore state from the user's home directory if it exists
        checkAndHandleGameOverState();
        board = new LGameBoard(model, this);

        // Top bar: title and instructions
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(15, 15, 10, 15));
        topBox.setAlignment(Pos.CENTER);

        titleLabel = new Label();
        titleLabel.getStyleClass().add("game-title");

        // Create Menu Bar
        MenuBar menuBar = new MenuBar();
        gameMenu = new Menu();
        helpMenu = new Menu();

        itemSwitchUnfinished = new MenuItem();
        itemEditNames = new MenuItem();
        itemFinishedList = new MenuItem();
        itemChooseLanguage = new MenuItem();
        itemQuit = new MenuItem();
        itemAbout = new MenuItem();
        itemHelp = new MenuItem();

        gameMenu.getItems().addAll(itemSwitchUnfinished, itemEditNames, itemFinishedList, itemChooseLanguage, itemQuit, itemAbout);
        helpMenu.getItems().add(itemHelp);
        menuBar.getMenus().addAll(gameMenu, helpMenu);

        // Bind Menu Actions
        itemSwitchUnfinished.setOnAction(e -> showUnfinishedGamesDialog());
        itemEditNames.setOnAction(e -> showEditNamesDialog());
        itemFinishedList.setOnAction(e -> showFinishedGamesDialog());
        itemChooseLanguage.setOnAction(e -> showLanguageDialog());
        itemQuit.setOnAction(e -> stage.fireEvent(new javafx.stage.WindowEvent(stage, javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST)));
        itemAbout.setOnAction(e -> showAboutDialog());
        itemHelp.setOnAction(e -> showHelpDialog());

        statusLabel = new Label();
        statusLabel.getStyleClass().addAll("status-alert", "alert");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setWrapText(true); // Enable text wrapping for multiline status

        topBox.getChildren().addAll(titleLabel, menuBar, statusLabel);

        // Center board wrapper
        StackPane boardWrapper = new StackPane();
        boardWrapper.getStyleClass().add("board-container");
        boardWrapper.setAlignment(Pos.CENTER);
        boardWrapper.getChildren().add(board);

        // Force strict fixed size for boardWrapper to prevent resizing or layout shifts
        double wrapperSize = board.getCellSize() * 4 + 30; // 360 board + 30 padding
        boardWrapper.setPrefSize(wrapperSize, wrapperSize);
        boardWrapper.setMinSize(wrapperSize, wrapperSize);
        boardWrapper.setMaxSize(wrapperSize, wrapperSize);

        BorderPane.setMargin(boardWrapper, new Insets(5, 20, 5, 20));

        // Bottom control buttons
        btnRotate = new Button();
        btnRotate.getStyleClass().addAll("btn", "btn-secondary");
        btnRotate.setOnAction(e -> board.rotateDraftPiece());

        btnFlip = new Button();
        btnFlip.getStyleClass().addAll("btn", "btn-secondary");
        btnFlip.setOnAction(e -> board.flipDraftPiece());

        btnConfirm = new Button();
        btnConfirm.getStyleClass().addAll("btn", "btn-success");
        btnConfirm.setOnAction(e -> {
            board.confirmDraftMove();
            updateUI();
        });

        btnSkip = new Button();
        btnSkip.getStyleClass().addAll("btn", "btn-warning");
        btnSkip.setOnAction(e -> {
            model.skipNeutralMove();
            board.resetDraftState();
            board.draw();
            updateUI();
        });

        btnReset = new Button();
        btnReset.getStyleClass().addAll("btn", "btn-danger");
        btnReset.setOnAction(e -> {
            if (model.getPhase() != LGameModel.GamePhase.GAME_OVER) {
                String lang = model.getLanguage();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(LanguageSupport.getTranslation("uusiPeliPromptTitle", lang));
                alert.setHeaderText(null);
                alert.setContentText(LanguageSupport.getTranslation("uusiPeliPromptText", lang));

                ButtonType buttonTypeYes = new ButtonType(LanguageSupport.getTranslation("kylla", lang), ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNo = new ButtonType(LanguageSupport.getTranslation("ei", lang), ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != buttonTypeYes) {
                    return; // Abort resetting
                }

                // Save current game state to unfinished games list before resetting
                model.saveCurrentToUnfinished();
            }
            gameOverAlertShown = false;
            model.resetGame();
            model.saveState(); // Save new game start state!
            board.resetDraftState();
            board.draw();
            updateUI();
        });

        HBox controlBox = new HBox(12);
        controlBox.getStyleClass().add("button-bar");
        controlBox.setAlignment(Pos.CENTER);
        controlBox.getChildren().addAll(btnRotate, btnFlip, btnConfirm, btnSkip, btnReset);

        // Error message label placed between board and buttons
        errorLabel = new Label();
        errorLabel.setId("error-label");
        errorLabel.getStyleClass().setAll("alert-empty");
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setWrapText(true);
        errorLabel.setMinHeight(45);
        errorLabel.setPrefHeight(45);
        errorLabel.setMaxHeight(45);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        // Layout assembly using VBox to guarantee no overlapping between board, errorLabel, and buttons
        VBox root = new VBox(12);
        root.getStyleClass().add("root-pane");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 20, 15, 20));
        root.getChildren().addAll(topBox, boardWrapper, errorLabel, controlBox);

        // Create Scene and load stylesheets
        Scene scene = new Scene(root, 560, 740);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        URL styleUrl2 = LGameApplication.class.getResource("/");
        URL styleUrl = LGameApplication.class.getResource("style.css");
        String customStyle = styleUrl.toExternalForm();
        if (customStyle != null) {
            scene.getStylesheets().add(customStyle);
        }

        // Add keyboard hotkeys
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    if (!btnRotate.isDisable()) btnRotate.fire();
                    break;
                case F:
                    if (!btnFlip.isDisable()) btnFlip.fire();
                    break;
                case V:
                case ENTER:
                    if (!btnConfirm.isDisable()) btnConfirm.fire();
                    break;
                case S:
                    if (!btnSkip.isDisable()) btnSkip.fire();
                    break;
                case N:
                    if (!btnReset.isDisable()) btnReset.fire();
                    break;
                case ESCAPE:
                case BACK_SPACE:
                case C:
                    board.undraftPiece();
                    break;
            }
        });

        stage.setTitle("L-Peli (L Game)");
        stage.setScene(scene);
        
        // Allow resizing and set initial size as minimum size (maximize button is enabled)
        stage.setResizable(true);
        stage.setMinWidth(560);
        stage.setMinHeight(760); // 760 includes typical window border titlebar height safely
        
        // Save game state to user's home directory on window close
        stage.setOnCloseRequest(event -> {
            model.saveState();
        });
        
        stage.show();

        // Repaint the board and initialize label texts
        board.draw();
        updateUI();
    }

    private void showEditNamesDialog() {
        String lang = model.getLanguage();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(LanguageSupport.getTranslation("pelaajienNimet", lang));
        dialog.setHeaderText(LanguageSupport.getTranslation("muokkaaNimia", lang));

        ButtonType saveButtonType = new ButtonType(LanguageSupport.getTranslation("tallenna", lang), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, new ButtonType(LanguageSupport.getTranslation("peruuta", lang), ButtonBar.ButtonData.CANCEL_CLOSE));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 40, 10, 20));

        TextField p1 = new TextField(model.getPlayer1Name());
        TextField p2 = new TextField(model.getPlayer2Name());

        grid.add(new Label(LanguageSupport.getTranslation("pelaaja1", lang) + " (" + LanguageSupport.getTranslation("punainen", lang) + "):"), 0, 0);
        grid.add(p1, 1, 0);
        grid.add(new Label(LanguageSupport.getTranslation("pelaaja2", lang) + " (" + LanguageSupport.getTranslation("sininen", lang) + "):"), 0, 1);
        grid.add(p2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            String name1 = p1.getText().trim();
            String name2 = p2.getText().trim();
            model.setPlayer1Name(name1.isEmpty() ? LanguageSupport.getTranslation("pelaaja1", lang) : name1);
            model.setPlayer2Name(name2.isEmpty() ? LanguageSupport.getTranslation("pelaaja2", lang) : name2);
            updateUI();
        }
    }

    private void showUnfinishedGamesDialog() {
        String lang = model.getLanguage();
        Dialog<SavedGameState> dialog = new Dialog<>();
        dialog.setTitle(LanguageSupport.getTranslation("keskeneraiset", lang));
        dialog.setHeaderText(LanguageSupport.getTranslation("listaKeskeneraiset", lang));

        ButtonType loadButtonType = new ButtonType(LanguageSupport.getTranslation("lataaPeli", lang), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loadButtonType, new ButtonType(LanguageSupport.getTranslation("peruuta", lang), ButtonBar.ButtonData.CANCEL_CLOSE));

        ListView<SavedGameState> listView = new ListView<>();
        listView.getItems().addAll(model.getUnfinishedGames());
        listView.setPrefWidth(240);
        listView.setPrefHeight(260);

        VBox previewCol = new VBox(12);
        previewCol.setAlignment(Pos.TOP_CENTER);
        previewCol.setPadding(new Insets(0, 10, 0, 10));
        previewCol.setPrefWidth(220);

        Label lblPreviewTitle = new Label(LanguageSupport.getTranslation("esikatselu", lang));
        lblPreviewTitle.setStyle("-fx-font-weight: bold;");

        StackPane previewWrapper = new StackPane();
        previewWrapper.setStyle("-fx-border-color: #4b5563; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 8; -fx-background-color: #c4b5fd;");
        previewWrapper.setMinSize(160, 160);
        previewWrapper.setPrefSize(160, 160);
        previewWrapper.setMaxSize(160, 160);
        previewWrapper.getChildren().add(createCompactPreviewBoard(null));

        Label lblDetails = new Label("");
        lblDetails.setWrapText(true);
        lblDetails.setPrefWidth(180);
        lblDetails.setMinHeight(90);
        lblDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #1f2937; -fx-font-weight: bold;");
        lblDetails.setAlignment(Pos.TOP_LEFT);

        previewCol.getChildren().addAll(lblPreviewTitle, previewWrapper, lblDetails);

        listView.setCellFactory(param -> new ListCell<SavedGameState>() {
            @Override
            protected void updateItem(SavedGameState item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    Label lblDate = new Label(item.saveDate);
                    lblDate.setStyle("-fx-font-weight: bold;");

                    Button btnDel = new Button("✖");
                    btnDel.setStyle("-fx-text-fill: #dc2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                    btnDel.setOnAction(e -> {
                        model.getUnfinishedGames().remove(item);
                        listView.getItems().remove(item);
                        previewWrapper.getChildren().clear();
                        previewWrapper.getChildren().add(createCompactPreviewBoard(null));
                        lblDetails.setText("");
                    });

                    cellBox.getChildren().addAll(btnDel, lblDate);
                    setGraphic(cellBox);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            previewWrapper.getChildren().clear();
            if (newVal != null) {
                previewWrapper.getChildren().add(createCompactPreviewBoard(newVal));
                String turnName = newVal.isRedTurn ? newVal.player1Name : newVal.player2Name;
                String playerLabel = LanguageSupport.getTranslation("pelaajienNimet", lang);
                String turnLabel = "Vuoro";
                if (lang.equals("en")) turnLabel = "Turn";
                if (lang.equals("sv")) turnLabel = "Tur";
                if (lang.equals("de")) turnLabel = "Zug";
                if (lang.equals("es")) turnLabel = "Turno";

                String redAbbr = lang.equals("fi") ? "Pun" : (lang.equals("en") ? "Red" : (lang.equals("sv") ? "Röd" : (lang.equals("de") ? "Rot" : "Rojo")));
                String blueAbbr = lang.equals("fi") ? "Sin" : (lang.equals("en") ? "Blue" : (lang.equals("sv") ? "Blå" : (lang.equals("de") ? "Blau" : "Azul")));

                lblDetails.setText(playerLabel + ":\n" + newVal.player1Name + " (" + redAbbr + ") vs\n" + newVal.player2Name + " (" + blueAbbr + ")\n\n" + turnLabel + ": " + turnName);
            } else {
                previewWrapper.getChildren().add(createCompactPreviewBoard(null));
                lblDetails.setText("");
            }
        });

        HBox content = new HBox(15);
        content.setPadding(new Insets(10));
        content.setMinWidth(480);
        content.setMinHeight(340);
        content.getChildren().addAll(listView, previewCol);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loadButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<SavedGameState> result = dialog.showAndWait();
        result.ifPresent(selectedGame -> {
            // Save the current game to unfinished games *only now* before loading the new one!
            model.saveCurrentToUnfinished();
            
            gameOverAlertShown = false;
            model.loadGameState(selectedGame);
            checkAndHandleGameOverState();
            model.saveState(); // Save active state immediately!
            board.resetDraftState();
            board.draw();
            updateUI();
        });
    }

    private void showFinishedGamesDialog() {
        String lang = model.getLanguage();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(LanguageSupport.getTranslation("paattyneet", lang));
        dialog.setHeaderText(LanguageSupport.getTranslation("listaPaattyneet", lang));
        dialog.getDialogPane().getButtonTypes().add(new ButtonType(LanguageSupport.getTranslation("peruuta", lang), ButtonBar.ButtonData.CANCEL_CLOSE));

        ListView<SavedGameState> listView = new ListView<>();
        listView.getItems().addAll(model.getFinishedGames());
        listView.setPrefWidth(240);
        listView.setPrefHeight(260);

        VBox previewCol = new VBox(12);
        previewCol.setAlignment(Pos.TOP_CENTER);
        previewCol.setPadding(new Insets(0, 10, 0, 10));
        previewCol.setPrefWidth(220);

        Label lblPreviewTitle = new Label(LanguageSupport.getTranslation("esikatselu", lang));
        lblPreviewTitle.setStyle("-fx-font-weight: bold;");

        StackPane previewWrapper = new StackPane();
        previewWrapper.setStyle("-fx-border-color: #4b5563; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 8; -fx-background-color: #c4b5fd;");
        previewWrapper.setMinSize(160, 160);
        previewWrapper.setPrefSize(160, 160);
        previewWrapper.setMaxSize(160, 160);
        previewWrapper.getChildren().add(createCompactPreviewBoard(null));

        Label lblDetails = new Label("");
        lblDetails.setWrapText(true);
        lblDetails.setPrefWidth(180);
        lblDetails.setMinHeight(90);
        lblDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #1f2937; -fx-font-weight: bold;");
        lblDetails.setAlignment(Pos.TOP_LEFT);

        previewCol.getChildren().addAll(lblPreviewTitle, previewWrapper, lblDetails);

        listView.setCellFactory(param -> new ListCell<SavedGameState>() {
            @Override
            protected void updateItem(SavedGameState item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    Label lblDate = new Label(item.saveDate);
                    lblDate.setStyle("-fx-font-weight: bold;");

                    Button btnDel = new Button("✖");
                    btnDel.setStyle("-fx-text-fill: #dc2626; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                    btnDel.setOnAction(e -> {
                        model.getFinishedGames().remove(item);
                        listView.getItems().remove(item);
                        previewWrapper.getChildren().clear();
                        previewWrapper.getChildren().add(createCompactPreviewBoard(null));
                        lblDetails.setText("");
                    });

                    cellBox.getChildren().addAll(btnDel, lblDate);
                    setGraphic(cellBox);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            previewWrapper.getChildren().clear();
            if (newVal != null) {
                previewWrapper.getChildren().add(createCompactPreviewBoard(newVal));
                String winnerName = newVal.winnerIsRed ? newVal.player1Name : newVal.player2Name;
                String playerLabel = LanguageSupport.getTranslation("pelaajienNimet", lang);
                String winnerLabel = lang.equals("fi") ? "Voittaja" : (lang.equals("en") ? "Winner" : (lang.equals("sv") ? "Vinnare" : (lang.equals("de") ? "Gewinner" : "Ganador")));

                String redAbbr = lang.equals("fi") ? "Pun" : (lang.equals("en") ? "Red" : (lang.equals("sv") ? "Röd" : (lang.equals("de") ? "Rot" : "Rojo")));
                String blueAbbr = lang.equals("fi") ? "Sin" : (lang.equals("en") ? "Blue" : (lang.equals("sv") ? "Blå" : (lang.equals("de") ? "Blau" : "Azul")));

                lblDetails.setText(playerLabel + ":\n" + newVal.player1Name + " (" + redAbbr + ") vs\n" + newVal.player2Name + " (" + blueAbbr + ")\n\n" + winnerLabel + ": " + winnerName);
            } else {
                previewWrapper.getChildren().add(createCompactPreviewBoard(null));
                lblDetails.setText("");
            }
        });

        HBox content = new HBox(15);
        content.setPadding(new Insets(10));
        content.setMinWidth(480);
        content.setMinHeight(340);
        content.getChildren().addAll(listView, previewCol);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void showAboutDialog() {
        String lang = model.getLanguage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageSupport.getTranslation("tietoaDialogTitle", lang));
        alert.setHeaderText("L Game (" + LanguageSupport.getTranslation("tietoa", lang) + ")");
        alert.setContentText(LanguageSupport.getTranslation("tietoaText", lang));
        alert.showAndWait();
    }

    private Pane createCompactPreviewBoard(SavedGameState state) {
        Pane preview = new Pane();
        double scale = 35.0;
        preview.setPrefSize(4 * scale, 4 * scale);
        preview.setMinSize(4 * scale, 4 * scale);
        preview.setMaxSize(4 * scale, 4 * scale);

        // Draw cells
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                Rectangle cell = new Rectangle(c * scale, r * scale, scale, scale);
                cell.setFill(Color.web("#ffc000"));
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(1.0);
                preview.getChildren().add(cell);
            }
        }

        if (state != null) {
            drawCompactLPiece(preview, state.redPiece, scale, Color.web("#dc2626"));
            drawCompactLPiece(preview, state.bluePiece, scale, Color.web("#2563eb"));
            drawCompactCoin(preview, state.neutral1, scale);
            drawCompactCoin(preview, state.neutral2, scale);
        }
        return preview;
    }

    private void drawCompactLPiece(Pane p, LPiece piece, double scale, Color color) {
        double[] coords = LPiece.getPolygonCoordinates(piece.getCx(), piece.getCy(), piece.getOrientation(), scale);
        if (coords.length > 0) {
            Polygon poly = new Polygon(coords);
            poly.setFill(color);
            poly.setStroke(null);
            p.getChildren().add(poly);
        }
    }

    private void drawCompactCoin(Pane p, Point coin, double scale) {
        double cx = coin.col * scale + scale / 2;
        double cy = coin.row * scale + scale / 2;
        double radius = scale * 0.35;

        Circle outer = new Circle(cx, cy, radius);
        outer.setFill(Color.web("#c4b5fd"));
        outer.setStroke(Color.BLACK);
        outer.setStrokeWidth(0.8);

        Circle inner = new Circle(cx, cy, radius * 0.65);
        inner.setFill(Color.BLACK);
        inner.setStroke(null);

        p.getChildren().addAll(outer, inner);
    }

    public void updateUI() {
        checkAndHandleGameOverState();
        String lang = model.getLanguage();
        LGameModel.GamePhase phase = model.getPhase();
        boolean isRed = model.isRedTurn();

        // Update title text
        titleLabel.setText(lang.equals("fi") ? "L-Peli" : "L Game");

        // Update Menus
        if (gameMenu != null) gameMenu.setText(LanguageSupport.getTranslation("valikko", lang));
        if (helpMenu != null) helpMenu.setText(LanguageSupport.getTranslation("apua", lang));
        if (itemSwitchUnfinished != null) itemSwitchUnfinished.setText(LanguageSupport.getTranslation("keskeneraiset", lang));
        if (itemEditNames != null) itemEditNames.setText(LanguageSupport.getTranslation("muokkaaNimia", lang));
        if (itemFinishedList != null) itemFinishedList.setText(LanguageSupport.getTranslation("paattyneet", lang));
        if (itemChooseLanguage != null) itemChooseLanguage.setText(LanguageSupport.getTranslation("valitseKieli", lang));
        if (itemQuit != null) itemQuit.setText(LanguageSupport.getTranslation("lopeta", lang));
        if (itemAbout != null) itemAbout.setText(LanguageSupport.getTranslation("tietoa", lang));
        if (itemHelp != null) itemHelp.setText(LanguageSupport.getTranslation("apuaDialogTitle", lang));

        // Update buttons
        if (btnRotate != null) btnRotate.setText(LanguageSupport.getTranslation("pyorita", lang));
        if (btnFlip != null) btnFlip.setText(LanguageSupport.getTranslation("peilaa", lang));
        if (btnConfirm != null) btnConfirm.setText(LanguageSupport.getTranslation("vahvista", lang));
        if (btnSkip != null) btnSkip.setText(LanguageSupport.getTranslation("ohita", lang));
        if (btnReset != null) btnReset.setText(LanguageSupport.getTranslation("uusiPeliBtn", lang));

        // Update status bar text and colors
        if (phase == LGameModel.GamePhase.GAME_OVER) {
            String winnerLabel = LanguageSupport.getTranslation("voittaja", lang);
            String redLabel = LanguageSupport.getTranslation("punainen", lang);
            String blueLabel = LanguageSupport.getTranslation("sininen", lang);
            String winner = model.isWinnerIsRed() ? model.getPlayer1Name() + " (" + redLabel + ")" : model.getPlayer2Name() + " (" + blueLabel + ")";
            
            String noMovesLostText = LanguageSupport.getTranslation("noMovesLost", lang);
            statusLabel.setText(noMovesLostText + " " + winnerLabel + winner);
            statusLabel.getStyleClass().setAll("status-alert", "alert", "alert-success");

            if (!gameOverAlertShown) {
                gameOverAlertShown = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(lang.equals("fi") ? "Peli päättyi" : (lang.equals("sv") ? "Spelet slut" : (lang.equals("de") ? "Spiel beendet" : (lang.equals("es") ? "Juego terminado" : "Game Over"))));
                alert.setHeaderText(noMovesLostText);
                alert.setContentText(winnerLabel + winner);
                ButtonType btnOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(btnOk);
                alert.showAndWait();
            }
        } else {
            String redLabel = LanguageSupport.getTranslation("punainen", lang);
            String blueLabel = LanguageSupport.getTranslation("sininen", lang);
            String turnPlayer = isRed ? model.getPlayer1Name() + " (" + redLabel + ")" : model.getPlayer2Name() + " (" + blueLabel + ")";
            String phaseText = (phase == LGameModel.GamePhase.L_MOVE)
                ? LanguageSupport.getTranslation("asetaL", lang)
                : LanguageSupport.getTranslation("siirraKolikko", lang);
            statusLabel.setText(turnPlayer + " - " + phaseText);

            if (isRed) {
                statusLabel.getStyleClass().setAll("status-alert", "alert", "alert-danger");
            } else {
                statusLabel.getStyleClass().setAll("status-alert", "alert", "alert-primary");
            }
        }

        // Handle error message for invalid draft placement (keep visible/managed to lock layout height)
        boolean hasError = false;
        String reason = null;
        if (phase == LGameModel.GamePhase.L_MOVE && board.isDraftPlaced()) {
            reason = board.getDraftInvalidReason();
            if (reason != null) {
                hasError = true;
            }
        }

        if (hasError) {
            errorLabel.setText(reason);
            errorLabel.getStyleClass().setAll("alert", "alert-danger");
        } else {
            errorLabel.setText("");
            errorLabel.getStyleClass().setAll("alert-empty");
        }

        // Enable / disable buttons dynamically
        if (phase == LGameModel.GamePhase.L_MOVE) {
            btnRotate.setDisable(false);
            btnFlip.setDisable(false);
            btnConfirm.setDisable(!board.isDraftValid());
            btnSkip.setDisable(true);
        } else if (phase == LGameModel.GamePhase.NEUTRAL_MOVE) {
            btnRotate.setDisable(true);
            btnFlip.setDisable(true);
            btnConfirm.setDisable(true);
            btnSkip.setDisable(false);
        } else { // GAME_OVER
            btnRotate.setDisable(true);
            btnFlip.setDisable(true);
            btnConfirm.setDisable(true);
            btnSkip.setDisable(true);
        }
    }

    private void showLanguageDialog() {
        String lang = model.getLanguage();
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(LanguageSupport.getTranslation("kieliDialogTitle", lang));
        dialog.setHeaderText(LanguageSupport.getTranslation("kieliDialogHeader", lang));

        ButtonType confirmButtonType = new ButtonType(LanguageSupport.getTranslation("tallenna", lang), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, new ButtonType(LanguageSupport.getTranslation("peruuta", lang), ButtonBar.ButtonData.CANCEL_CLOSE));

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        ToggleGroup group = new ToggleGroup();
        RadioButton rbFi = new RadioButton("Suomi fi");
        rbFi.setToggleGroup(group);
        rbFi.setUserData("fi");

        RadioButton rbEn = new RadioButton("English en");
        rbEn.setToggleGroup(group);
        rbEn.setUserData("en");

        RadioButton rbSv = new RadioButton("Svenska sv");
        rbSv.setToggleGroup(group);
        rbSv.setUserData("sv");

        RadioButton rbDe = new RadioButton("Deutsch de");
        rbDe.setToggleGroup(group);
        rbDe.setUserData("de");

        RadioButton rbEs = new RadioButton("Español es");
        rbEs.setToggleGroup(group);
        rbEs.setUserData("es");

        switch (lang) {
            case "en": rbEn.setSelected(true); break;
            case "sv": rbSv.setSelected(true); break;
            case "de": rbDe.setSelected(true); break;
            case "es": rbEs.setSelected(true); break;
            default: rbFi.setSelected(true); break;
        }

        box.getChildren().addAll(rbFi, rbEn, rbSv, rbDe, rbEs);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return (String) group.getSelectedToggle().getUserData();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedLang -> {
            model.setLanguage(selectedLang);
            model.saveState();
            updateUI();
        });
    }

    private void showHelpDialog() {
        String lang = model.getLanguage();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(LanguageSupport.getTranslation("apuaDialogTitle", lang));
        dialog.getDialogPane().getButtonTypes().add(new ButtonType(LanguageSupport.getTranslation("peruuta", lang), ButtonBar.ButtonData.CANCEL_CLOSE));

        WebView webView = new WebView();
        webView.setPrefSize(720, 600);

        URL helpUrl = getClass().getResource("help/help_" + lang + ".html");
        if (helpUrl != null) {
            webView.getEngine().load(helpUrl.toExternalForm());
        } else {
            URL defaultUrl = getClass().getResource("help/help_en.html");
            if (defaultUrl != null) {
                webView.getEngine().load(defaultUrl.toExternalForm());
            }
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    private void checkAndHandleGameOverState() {
        if (model.getPhase() != LGameModel.GamePhase.GAME_OVER && !model.hasAnyLegalMoves(model.isRedTurn())) {
            model.setPhase(LGameModel.GamePhase.GAME_OVER);
            model.setWinnerIsRed(!model.isRedTurn());
            
            SavedGameState finishedGame = new SavedGameState(
                String.valueOf(System.currentTimeMillis()),
                new LPiece(model.getRedPiece().getCx(), model.getRedPiece().getCy(), model.getRedPiece().getOrientation(), model.getRedPiece().isRed()),
                new LPiece(model.getBluePiece().getCx(), model.getBluePiece().getCy(), model.getBluePiece().getOrientation(), model.getBluePiece().isRed()),
                new Point(model.getNeutral1().col, model.getNeutral1().row),
                new Point(model.getNeutral2().col, model.getNeutral2().row),
                model.isRedTurn(),
                model.getPhase(),
                model.isWinnerIsRed(),
                model.getPlayer1Name(),
                model.getPlayer2Name()
            );
            
            boolean alreadyExists = false;
            for (SavedGameState old : model.getFinishedGames()) {
                if (old.redPiece.getCx() == finishedGame.redPiece.getCx() &&
                    old.redPiece.getCy() == finishedGame.redPiece.getCy() &&
                    old.redPiece.getOrientation() == finishedGame.redPiece.getOrientation() &&
                    old.bluePiece.getCx() == finishedGame.bluePiece.getCx() &&
                    old.bluePiece.getCy() == finishedGame.bluePiece.getCy() &&
                    old.bluePiece.getOrientation() == finishedGame.bluePiece.getOrientation() &&
                    old.neutral1.equals(finishedGame.neutral1) &&
                    old.neutral2.equals(finishedGame.neutral2)) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                model.getFinishedGames().add(finishedGame);
            }
            model.saveState();
        }
    }
}

