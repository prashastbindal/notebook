package services;

import io.javalin.Javalin;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.io.File;

public class TesseractTextExtractor implements OCRTextExtractor{

    private ITesseract instance;

    public TesseractTextExtractor() {
        instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata_min/");
        instance.setLanguage("eng+equ");
        ImageIO.scanForPlugins();
    }

    /**
     * Extract text from the specified file path
     *
     * @param path : path to file from which to extract text
     */
    public String extractText(String path) {

        Javalin.log.info("PDF fn: " + path);
        File pdf = new File(path);
        try {
            String fulltext = instance.doOCR(pdf);
            Javalin.log.info("Completed OCR.");
            Javalin.log.info(fulltext.substring(0, 10) + "...");
            return fulltext;
        } catch (TesseractException e) {
            Javalin.log.info("OCR Failed.");
            Javalin.log.info(e.getMessage());
            return null;
        }
    }

}
