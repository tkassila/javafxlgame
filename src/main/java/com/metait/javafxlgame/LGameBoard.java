package com.metait.javafxlgame;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;

import java.util.List;

public class LGameBoard extends Pane {
    private final LGameModel model;
    private final LGameApplication app;
    private final double cellSize = 90.0;

    public double getCellSize() {
        return cellSize;
    }

    public boolean isDraftPlaced() {
        return isDraftPlaced;
    }

    // L-piece move draft state
    private int draftCornerX;
    private int draftCornerY;
    private int draftOrientation;
    private boolean isDraftPlaced;
    private boolean wasDraftPlacedBeforeClick;

    public LGameBoard(LGameModel model, LGameApplication app) {
        this.model = model;
        this.app = app;

        // Set dimensions
        setPrefSize(4 * cellSize, 4 * cellSize);
        setMinSize(4 * cellSize, 4 * cellSize);
        setMaxSize(4 * cellSize, 4 * cellSize);

        // Strict clip to prevent layout bounds stretching from effects/shadows
        setClip(new Rectangle(4 * cellSize, 4 * cellSize));

        // Reset draft status to match current game state
        resetDraftState();

        // Mouse scroll listener to rotate piece
        setOnScroll(e -> {
            if (model.getPhase() == LGameModel.GamePhase.L_MOVE) {
                rotateDraftPiece();
                e.consume();
            }
        });

        // Mouse move listener for ghost/preview
        setOnMouseMoved(e -> {
            if (model.getPhase() == LGameModel.GamePhase.L_MOVE && !isDraftPlaced) {
                int col = (int) (e.getX() / cellSize);
                int row = (int) (e.getY() / cellSize);
                Point clamped = clampCorner(col, row, draftOrientation);

                if (clamped.col != draftCornerX || clamped.row != draftCornerY) {
                    draftCornerX = clamped.col;
                    draftCornerY = clamped.row;
                    draw();
                    app.updateUI();
                }
            }
        });

        // Mouse click listener for drafting L-piece and moving neutral pieces
        setOnMouseClicked(e -> {
            int col = (int) (e.getX() / cellSize);
            int row = (int) (e.getY() / cellSize);

            if (col < 0 || col >= 4 || row < 0 || row >= 4) return;

            Point p = new Point(col, row);

            if (model.getPhase() == LGameModel.GamePhase.L_MOVE) {
                // If it's the first click of a sequence, record the state
                if (e.getClickCount() == 1) {
                    wasDraftPlacedBeforeClick = isDraftPlaced;
                }

                // Check if double click on the L-piece move frame cells
                if (e.getClickCount() == 2) {
                    List<Point> occupied = LPiece.getOccupiedCells(draftCornerX, draftCornerY, draftOrientation);
                    if (occupied.contains(p)) {
                        flipDraftPiece(); // Cycles orientation
                        isDraftPlaced = wasDraftPlacedBeforeClick; // Restore original draft state
                        draw();
                        app.updateUI();
                        return;
                    }
                }

                // Propose draft-place at clicked clamped position (allow placing invalid moves to show reasons)
                Point clamped = clampCorner(col, row, draftOrientation);
                draftCornerX = clamped.col;
                draftCornerY = clamped.row;
                isDraftPlaced = true;
                draw();
                app.updateUI();
            } else if (model.getPhase() == LGameModel.GamePhase.NEUTRAL_MOVE) {
                // If clicked a neutral piece, select it
                if (p.equals(model.getNeutral1()) || p.equals(model.getNeutral2())) {
                    model.selectNeutralPiece(p);
                    draw();
                    app.updateUI();
                } else if (model.getSelectedNeutral() != null) {
                    // Try to move selected neutral to clicked cell
                    if (model.moveSelectedNeutralTo(p)) {
                        resetDraftState();
                        draw();
                        app.updateUI();
                    }
                }
            }
        });
    }

    public void resetDraftState() {
        isDraftPlaced = false;
        if (model.getPhase() == LGameModel.GamePhase.L_MOVE) {
            LPiece activePiece = model.isRedTurn() ? model.getRedPiece() : model.getBluePiece();
            draftOrientation = activePiece.getOrientation();
            draftCornerX = activePiece.getCx();
            draftCornerY = activePiece.getCy();
        }
    }

    public boolean isDraftValid() {
        return model.isValidLMove(draftCornerX, draftCornerY, draftOrientation, model.isRedTurn());
    }

    public String getDraftInvalidReason() {
        return model.getInvalidLMoveReason(draftCornerX, draftCornerY, draftOrientation, model.isRedTurn());
    }

    private final int[] verticalSet = {0, 1, 5, 4};
    private final int[] horizontalSet = {2, 3, 7, 6};

    private boolean isVertical(int orientation) {
        for (int v : verticalSet) {
            if (v == orientation) return true;
        }
        return false;
    }

    private int getIndexInSet(int orientation, int[] set) {
        for (int i = 0; i < set.length; i++) {
            if (set[i] == orientation) return i;
        }
        return 0;
    }

    public void rotateDraftPiece() {
        if (model.getPhase() != LGameModel.GamePhase.L_MOVE) return;

        // Toggle vertical/horizontal and center the piece
        if (isVertical(draftOrientation)) {
            int idx = getIndexInSet(draftOrientation, verticalSet);
            draftOrientation = horizontalSet[idx];
            // Center horizontally
            Point clamped = clampCorner(1, 1, draftOrientation);
            draftCornerX = clamped.col;
            draftCornerY = clamped.row;
        } else {
            int idx = getIndexInSet(draftOrientation, horizontalSet);
            draftOrientation = verticalSet[idx];
            // Center vertically
            Point clamped = clampCorner(1, 2, draftOrientation);
            draftCornerX = clamped.col;
            draftCornerY = clamped.row;
        }
        isDraftPlaced = true; // Lock in center draft mode

        draw();
        app.updateUI();
    }

    public void flipDraftPiece() {
        if (model.getPhase() != LGameModel.GamePhase.L_MOVE) return;

        // Cycle to the next orientation in the current set
        if (isVertical(draftOrientation)) {
            int idx = getIndexInSet(draftOrientation, verticalSet);
            int nextIdx = (idx + 1) % verticalSet.length;
            draftOrientation = verticalSet[nextIdx];
        } else {
            int idx = getIndexInSet(draftOrientation, horizontalSet);
            int nextIdx = (idx + 1) % horizontalSet.length;
            draftOrientation = horizontalSet[nextIdx];
        }

        // Re-clamp around current position
        Point clamped = clampCorner(draftCornerX, draftCornerY, draftOrientation);
        draftCornerX = clamped.col;
        draftCornerY = clamped.row;
        isDraftPlaced = true; // Stay in draft mode

        draw();
        app.updateUI();
    }

    public void confirmDraftMove() {
        if (model.getPhase() != LGameModel.GamePhase.L_MOVE) return;
        if (isDraftValid()) {
            model.confirmLMove(draftCornerX, draftCornerY, draftOrientation);
            draw();
            app.updateUI();
        }
    }

    private Point clampCorner(int col, int row, int orientation) {
        List<Point> offsets = LPiece.getOffsets(orientation);
        int minCol = 0, maxCol = 0, minRow = 0, maxRow = 0;
        for (Point offset : offsets) {
            if (offset.col < minCol) minCol = offset.col;
            if (offset.col > maxCol) maxCol = offset.col;
            if (offset.row < minRow) minRow = offset.row;
            if (offset.row > maxRow) maxRow = offset.row;
        }
        int cx = Math.max(-minCol, Math.min(3 - maxCol, col));
        int cy = Math.max(-minRow, Math.min(3 - maxRow, row));
        return new Point(cx, cy);
    }

    public void draw() {
        getChildren().clear();

        // 1. Draw Board Grid Cells (Uniform Golden Yellow with Black Borders)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Rectangle cell = new Rectangle(col * cellSize, row * cellSize, cellSize, cellSize);
                cell.setFill(Color.web("#ffc000")); // Warm, deep golden yellow background
                cell.setStroke(Color.BLACK); // Grid lines are black
                cell.setStrokeWidth(1.0);
                getChildren().add(cell);
            }
        }

        // 2. Draw L-Pieces Normally (At full opacity)
        drawLPiece(model.getRedPiece(), 1.0);
        drawLPiece(model.getBluePiece(), 1.0);

        // 3. Draw Neutral Pieces (Circles)
        drawNeutralPiece(model.getNeutral1(), 1);
        drawNeutralPiece(model.getNeutral2(), 2);

        // 4. Draw Active Moving Piece on Top as an Outline Frame (Only during L_MOVE phase)
        if (model.getPhase() == LGameModel.GamePhase.L_MOVE) {
            drawMoveFrame(draftCornerX, draftCornerY, draftOrientation, model.isRedTurn(), isDraftPlaced);
        }
    }

    private void drawLPiece(LPiece piece, double opacity) {
        double[] coords = LPiece.getPolygonCoordinates(piece.getCx(), piece.getCy(), piece.getOrientation(), cellSize);
        if (coords.length == 0) return;

        Polygon poly = new Polygon(coords);
        // Clear, solid red for player 1, solid blue for player 2
        Color playerColor = piece.isRed() ? Color.web("#dc2626") : Color.web("#2563eb");

        poly.setFill(playerColor);
        poly.setOpacity(opacity);
        poly.setStroke(null); // Remove violet border from L-pieces

        if (opacity > 0.5) {
            DropShadow ds = new DropShadow(8, 0, 4, Color.color(0, 0, 0, 0.25));
            poly.setEffect(ds);
        }
        getChildren().add(poly);
    }

    private void drawMoveFrame(int cx, int cy, int orientation, boolean isRed, boolean isPlaced) {
        double[] coords = LPiece.getPolygonCoordinates(cx, cy, orientation, cellSize);
        if (coords.length == 0) return;

        Polygon frame = new Polygon(coords);

        // Center is completely transparent
        frame.setFill(Color.TRANSPARENT);
        frame.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);

        boolean isValid = isDraftValid();
        Color strokeColor = Color.web("#000000", 0.50); // Bounded by partially transparent black border

        frame.setStroke(strokeColor);

        if (isPlaced) {
            // Placed draft frame: 1.5x thicker black border (7.5px)
            frame.setStroke(Color.web("#000000", 0.70));
            frame.setStrokeWidth(7.5);

            // Double border look: inner dashed white border
            Polygon innerFrame = new Polygon(coords);
            innerFrame.setFill(Color.TRANSPARENT);
            innerFrame.setStroke(Color.WHITE);
            innerFrame.setStrokeWidth(1.5);
            innerFrame.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
            innerFrame.getStrokeDashArray().addAll(6.0, 4.0);

            Group group = new Group(frame, innerFrame);
            group.setEffect(new DropShadow(8, Color.color(0, 0, 0, 0.3)));
            getChildren().add(group);
        } else {
            // Hover preview frame
            if (isValid) {
                // 1.5x thicker stroke (6.75px)
                frame.setStrokeWidth(6.75);
                frame.setEffect(new DropShadow(6, Color.color(0, 0, 0, 0.2)));
                getChildren().add(frame);
            } else {
                // Invalid hover: 1.5x thicker dashed border (6.0px)
                frame.setStrokeWidth(6.0);
                frame.getStrokeDashArray().addAll(8.0, 4.0);
                getChildren().add(frame);
            }
        }

        // Draw Player Number (1 or 2) in the center (middle) cell of the frame
        List<Point> occupied = LPiece.getOccupiedCells(cx, cy, orientation);
        if (occupied.size() > 1) {
            Point midCell = occupied.get(1); // Middle cell is index 1
            javafx.scene.text.Text txt = new javafx.scene.text.Text(isRed ? "1" : "2");
            txt.setStyle("-fx-font-family: 'Inter', 'Segoe UI', sans-serif; -fx-font-size: 38px; -fx-font-weight: bold; -fx-fill: #ffffff;");
            txt.setTextOrigin(javafx.geometry.VPos.CENTER);
            txt.setEffect(new DropShadow(6, Color.BLACK));

            // Center inside the cell
            double tx = midCell.col * cellSize + cellSize / 2;
            double ty = midCell.row * cellSize + cellSize / 2;
            txt.setX(tx - txt.getLayoutBounds().getWidth() / 2);
            txt.setY(ty);

            getChildren().add(txt);
        }
    }

    private void drawNeutralPiece(Point p, int num) {
        double cx = p.col * cellSize + cellSize / 2;
        double cy = p.row * cellSize + cellSize / 2;
        double radius = cellSize * 0.35;

        Color ringColor;
        javafx.scene.effect.Effect glow = null;

        if (model.getPhase() == LGameModel.GamePhase.NEUTRAL_MOVE) {
            Point selected = model.getSelectedNeutral();
            if (selected != null && selected.equals(p)) {
                // Only the selected coin gets the player's turn color in its ring
                ringColor = model.isRedTurn() ? Color.web("#dc2626") : Color.web("#2563eb");
                glow = new DropShadow(15, Color.web("#eab308"));
            } else {
                // Unselected coins keep the neutral violet ring but glow softly to show they are clickable
                ringColor = Color.web("#c4b5fd");
                glow = new DropShadow(8, Color.web("#fbbf24"));
            }
        } else {
            ringColor = Color.web("#c4b5fd");
            glow = new DropShadow(6, 0, 3, Color.color(0, 0, 0, 0.3));
        }

        Circle outer = new Circle(cx, cy, radius);
        outer.setFill(ringColor);
        outer.setStroke(Color.BLACK);
        outer.setStrokeWidth(1.2);
        if (glow != null) {
            outer.setEffect(glow);
        }

        Circle inner = new Circle(cx, cy, radius * 0.65);
        inner.setFill(Color.BLACK);
        inner.setStroke(null);

        getChildren().addAll(outer, inner);
    }

    public void undraftPiece() {
        if (model.getPhase() == LGameModel.GamePhase.L_MOVE && isDraftPlaced) {
            isDraftPlaced = false;
            // Place back to mouse cursor position
            draw();
            app.updateUI();
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        return 4 * cellSize;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 4 * cellSize;
    }

    @Override
    protected double computePrefWidth(double height) {
        return 4 * cellSize;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 4 * cellSize;
    }

    @Override
    protected double computeMaxWidth(double height) {
        return 4 * cellSize;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return 4 * cellSize;
    }
}
