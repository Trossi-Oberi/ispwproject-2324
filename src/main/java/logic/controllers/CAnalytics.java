package logic.controllers;

import logic.beans.BAnalytics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class CAnalytics {
    public boolean exportAnalyticsFile(BAnalytics analysis) {
        String folderPath = "exportedAnalytics";
        String fileName = analysis.getEventName() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(folderPath + "/" + fileName))) {
            // Popola il file di testo con le informazioni dal bean BAnalysis

            writer.write("++++++++++++++Analytics of " + analysis.getEventName() + " ++++++++++++++\n\n");
            writer.write("Event Name: " + analysis.getEventName() + "\n");
            writer.write("Province: " + analysis.getEventProvince() + "\n");
            writer.write("City: " + analysis.getEventCity() + "\n");
            writer.write("Address: " + analysis.getEventAddress() + "\n");
            writer.write("Music genre: " + analysis.getEventMusicGenre() + "\n");
            writer.write("Date: " + analysis.getEventDate() + "\n");
            writer.write("Time: " + analysis.getEventTime() + "\n");
            writer.write("Times clicked: " + analysis.getTimesClicked() + "\n");
            writer.write("Participants: " + analysis.getParticipants() + "\n");
            writer.write("Planned participants: " + analysis.getPlannedParticipations() + "\n");
            writer.write("\n\n++++++++++++++End of file++++++++++++++");

            logger.log(Level.FINEST, () -> "Analysis exported successfully: "+folderPath+"/"+fileName);
            return true;
        } catch (IOException e) {
            logger.severe("Error while exporting .txt file: " + e.getMessage());
            return false;
        }
    }
}
