module com.metait.javafxlgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.metait.javafxlgame to javafx.fxml;
    exports com.metait.javafxlgame;
}