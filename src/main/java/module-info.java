module trossi.oberi {
    opens logic.graphiccontrollers;

    requires java.base;
    requires java.net.http;
    requires java.desktop;
    requires java.logging;
    requires transitive java.sql;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires transitive javafx.graphics;

    requires org.json;
    requires javafx.web;

    exports logic.view;
    exports logic.graphiccontrollers;

}