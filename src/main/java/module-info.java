module com.metait.javafxlgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.metait.javafxlgame to javafx.fxml;
    exports com.metait.javafxlgame;
}