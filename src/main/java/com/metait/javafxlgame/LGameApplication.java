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

public class HelloApplication extends Application {
    private LGameModel model;
    private LGameBoard board;

    private Label statusLabel;
    private Label errorLabel;
    private Button btnRotate;
    private Button btnFlip;
    private Button btnConfirm;
    private Button btnSkip;
    private Button btnReset;

    @Override
    public void start(Stage stage) {
        model = new LGameModel();
        model.loadState(); // Restore state from the user's home directory if it exists
        board = new LGameBoard(model, this);

        // Top bar: title and instructions
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(15, 15, 10, 15));
        topBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("L-Peli (L Game)");
        titleLabel.getStyleClass().add("game-title");

        // Create Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Valikko");

        MenuItem itemSwitchUnfinished = new MenuItem("Valitse toista keskeneräistä peliä");
        MenuItem itemEditNames = new MenuItem("Muokkaa pelaajien nimiä");
        MenuItem itemFinishedList = new MenuItem("Päättyneet pelit");
        MenuItem itemQuit = new MenuItem("Lopeta peli");
        MenuItem itemAbout = new MenuItem("Tietoa pelistä");

        gameMenu.getItems().addAll(itemSwitchUnfinished, itemEditNames, itemFinishedList, itemQuit, itemAbout);
        menuBar.getMenus().add(gameMenu);

        // Bind Menu Actions
        itemSwitchUnfinished.setOnAction(e -> showUnfinishedGamesDialog());
        itemEditNames.setOnAction(e -> showEditNamesDialog());
        itemFinishedList.setOnAction(e -> showFinishedGamesDialog());
        itemQuit.setOnAction(e -> stage.fireEvent(new javafx.stage.WindowEvent(stage, javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST)));
        itemAbout.setOnAction(e -> showAboutDialog());

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
        btnRotate = new Button("Pyöritä (R)");
        btnRotate.getStyleClass().addAll("btn", "btn-secondary");
        btnRotate.setOnAction(e -> board.rotateDraftPiece());

        btnFlip = new Button("Peilaa (F)");
        btnFlip.getStyleClass().addAll("btn", "btn-secondary");
        btnFlip.setOnAction(e -> board.flipDraftPiece());

        btnConfirm = new Button("Vahvista (V)");
        btnConfirm.getStyleClass().addAll("btn", "btn-success");
        btnConfirm.setOnAction(e -> {
            board.confirmDraftMove();
            updateUI();
        });

        btnSkip = new Button("Ohita (S)");
        btnSkip.getStyleClass().addAll("btn", "btn-warning");
        btnSkip.setOnAction(e -> {
            model.skipNeutralMove();
            board.resetDraftState();
            board.draw();
            updateUI();
        });

        btnReset = new Button("Uusi peli (N)");
        btnReset.getStyleClass().addAll("btn", "btn-danger");
        btnReset.setOnAction(e -> {
            if (model.getPhase() != LGameModel.GamePhase.GAME_OVER) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uusi peli");
                alert.setHeaderText(null);
                alert.setContentText("Aloitetaanko uusi L peli?");

                ButtonType buttonTypeYes = new ButtonType("Kyllä");
                ButtonType buttonTypeNo = new ButtonType("Ei");
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != buttonTypeYes) {
                    return; // Abort resetting
                }

                // Save current game state to unfinished games list before resetting
                model.saveCurrentToUnfinished();
            }
            model.resetGame();
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
        
        String customStyle = HelloApplication.class.getResource("style.css").toExternalForm();
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Pelaajien nimet");
        dialog.setHeaderText("Muokkaa pelaajien nimiä");

        ButtonType saveButtonType = new ButtonType("Tallenna", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 40, 10, 20));

        TextField p1 = new TextField(model.getPlayer1Name());
        TextField p2 = new TextField(model.getPlayer2Name());

        grid.add(new Label("Pelaaja 1 (Punainen):"), 0, 0);
        grid.add(p1, 1, 0);
        grid.add(new Label("Pelaaja 2 (Sininen):"), 0, 1);
        grid.add(p2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            String name1 = p1.getText().trim();
            String name2 = p2.getText().trim();
            model.setPlayer1Name(name1.isEmpty() ? "Pelaaja 1" : name1);
            model.setPlayer2Name(name2.isEmpty() ? "Pelaaja 2" : name2);
            updateUI();
        }
    }

    private void showUnfinishedGamesDialog() {
        Dialog<SavedGameState> dialog = new Dialog<>();
        dialog.setTitle("Keskeneräiset pelit");
        dialog.setHeaderText("Valitse toinen keskeneräinen peli");

        ButtonType loadButtonType = new ButtonType("Lataa peli", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loadButtonType, ButtonType.CANCEL);

        ListView<SavedGameState> listView = new ListView<>();
        listView.getItems().addAll(model.getUnfinishedGames());
        listView.setPrefWidth(240);
        listView.setPrefHeight(260);

        VBox previewCol = new VBox(12);
        previewCol.setAlignment(Pos.TOP_CENTER);
        previewCol.setPadding(new Insets(0, 10, 0, 10));
        previewCol.setPrefWidth(220);

        Label lblPreviewTitle = new Label("Esikatselu");
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
                lblDetails.setText("Pelaajat:\n" + newVal.player1Name + " (Pun) vs\n" + newVal.player2Name + " (Sin)\n\nVuoro: " + turnName);
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
            
            model.loadGameState(selectedGame);
            board.resetDraftState();
            board.draw();
            updateUI();
        });
    }

    private void showFinishedGamesDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Päättyneet pelit");
        dialog.setHeaderText("Lista päättyneistä peleistä");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ListView<SavedGameState> listView = new ListView<>();
        listView.getItems().addAll(model.getFinishedGames());
        listView.setPrefWidth(240);
        listView.setPrefHeight(260);

        VBox previewCol = new VBox(12);
        previewCol.setAlignment(Pos.TOP_CENTER);
        previewCol.setPadding(new Insets(0, 10, 0, 10));
        previewCol.setPrefWidth(220);

        Label lblPreviewTitle = new Label("Esikatselu");
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
                lblDetails.setText("Pelaajat:\n" + newVal.player1Name + " (Pun) vs\n" + newVal.player2Name + " (Sin)\n\nVoittaja: " + winnerName);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tietoa pelistä");
        alert.setHeaderText("L Game (L-Peli)");
        alert.setContentText("Versio: 1.0\nTekijä: Edward de Bono (Keksijä)\nSovelluskehitys: Metait / Antigravity AI\n\nL-peli on kahden pelaajan strategiapeli 4x4-ruudukolla.");
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
        Circle c = new Circle(cx, cy, scale * 0.35);
        c.setFill(Color.web("#4b5563"));
        c.setStroke(Color.web("#c4b5fd"));
        c.setStrokeWidth(1.5);
        p.getChildren().add(c);
    }

    public void updateUI() {
        LGameModel.GamePhase phase = model.getPhase();
        boolean isRed = model.isRedTurn();

        // Update status bar text and colors
        if (phase == LGameModel.GamePhase.GAME_OVER) {
            String winner = model.isWinnerIsRed() ? model.getPlayer1Name() + " (Punainen)" : model.getPlayer2Name() + " (Sininen)";
            statusLabel.setText("PELI PÄÄTTYI! Voittaja: " + winner);
            statusLabel.getStyleClass().setAll("status-alert", "alert", "alert-success");
        } else {
            String turnPlayer = isRed ? model.getPlayer1Name() + " (Punainen)" : model.getPlayer2Name() + " (Sininen)";
            String phaseText = (phase == LGameModel.GamePhase.L_MOVE)
                ? "Aseta L-nappula (R=Pyöritä, F=Peilaa, klikkaa + V=Vahvista)"
                : "Siirrä kolikkoa (klikkaa kolikkoa + tyhjää ruutua tai S=Ohita)";
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
}

