package fileserver;

import io.javalin.Javalin;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.File;
import java.util.Iterator;

import com.github.jaiimageio.plugins.tiff.TIFFImageReadParam;

public class OCRTextExtractor {

    private ITesseract instance;

    public OCRTextExtractor() {
        instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata_min/");
        instance.setLanguage("eng+equ");

        try {
            Class.forName("com.github.jaiimageio.plugins.tiff.TIFFImageReadParam");
        } catch (ClassNotFoundException e) {}

        ImageIO.scanForPlugins();
    }

    public String extractText(String fn) {

        Javalin.log.info("Readers");
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("tiff");

        try {
            ImageReader reader = readers.next();
            Javalin.log.info(reader.toString());
        } catch (Exception e) {
            Javalin.log.info("Failed to find...");
            e.printStackTrace();
        }

        Javalin.log.info("PDF fn: " + fn);
        File pdf = new File(fn);
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
