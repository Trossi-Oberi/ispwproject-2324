module trossi.oberi {
    opens logic.graphiccontrollers;
    opens logic.controllers;
    opens logic.utils;
    opens logic.beans;
    opens logic.model;
    opens logic.view;
    opens logic.server;
    opens logic.interfaces;

    requires java.base;
    requires java.net.http;
    requires java.desktop;
    requires java.logging;
    requires transitive java.sql;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires transitive javafx.graphics;

    requires org.json;

    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.json.jackson2;
    requires com.opencsv;

    exports logic.view;
    exports logic.graphiccontrollers;
    exports logic.controllers;
    exports logic.utils;
    exports logic.beans;
    exports logic.exceptions;
    exports logic.interfaces;
    exports logic.server;
    exports logic.model;

}