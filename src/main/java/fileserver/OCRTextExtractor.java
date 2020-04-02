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
import com.github.jaiimageio.impl.common.ImageUtil;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi;

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
        ImageIO.scanForPlugins();
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("tiff");
        try {
            ImageReader reader = readers.next();
            Javalin.log.info(reader.toString());
        } catch (Exception e) {
            Javalin.log.info("Failed to find tiff reader...");
            e.printStackTrace();
        }
        readers = ImageIO.getImageReadersByFormatName("tif");
        try {
            ImageReader reader = readers.next();
            Javalin.log.info(reader.toString());
        } catch (Exception e) {
            Javalin.log.info("Failed to find tif reader...");
            e.printStackTrace();
        }
        readers = ImageIO.getImageReadersByFormatName("jpg");
        try {
            ImageReader reader = readers.next();
            Javalin.log.info(reader.toString());
        } catch (Exception e) {
            Javalin.log.info("Failed to find jpg reader...");
            e.printStackTrace();
        }
        try {
            ImageReader reader = new TIFFImageReader(new TIFFImageReaderSpi());
            Javalin.log.info(reader.toString());
        } catch (Exception e) {
            Javalin.log.info("Failed to create tiff reader...");
            e.printStackTrace();
        }

        Javalin.log.info("PDF fn: " + fn);
        File pdf = new File(fn);
        try {
            ImageIO.scanForPlugins();
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
