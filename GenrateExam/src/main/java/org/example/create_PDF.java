package org.example;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class create_PDF {

    public static final String DEST = getCurrentDestination() + "/PhysicsTest.pdf";

    private static String getCurrentDestination() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, MalformedURLException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        if (args.length != 1) throw new IllegalArgumentException("number of arguments is incorrect");
        new create_PDF().createPdf(DEST, args[0]);
    }

    public void createPdf(String dest, String images) throws IOException, FileNotFoundException, MalformedURLException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);
        Image einstein = new Image(ImageDataFactory.create(images + "\\albert.jpg")).setAutoScale(true);
        Paragraph paragraph1 = new Paragraph("""
                Physics exam:
                 The time for the exam is 3 hours.
                 The use of a calculator is forbidden during the test. You may use the formulas sheet.
                 The exam contains 3 questions. Good luck!\s""");
        //Add paragraph to the document
        document.add(paragraph1);
        document.add(einstein);
        List<String> imageNames = getImages(images);
        document.add(new AreaBreak());
        document.add(new Paragraph("Questions:").setBold().setFontSize(65));
        generateQuestions(document, imageNames, true, images);
        document.add(new AreaBreak());
        document.add(new Paragraph("Answers:\n" +
                "the answers are below this page.").setBold().setFontSize(50));
        generateAnswers(document, imageNames, images);
        //Close document
        document.close();
    }

    private void generateAnswers(Document document, List<String> imageNames, String imagesRoot) throws MalformedURLException {
        generateQuestions(document, imageNames, false, imagesRoot);
    }

    private void generateQuestions(Document document, List<String> imageNames, boolean isQuestion, String imagesRoot) throws MalformedURLException {
        int questionNumber = 1;
        for (String imageName : imageNames) {
            generateQuestion(document, imageName, isQuestion, questionNumber++, imagesRoot);
        }
    }

    private List<String> getImages(String imagesRoot) {
        int numberOfQuestions = getNumberOfQuestions(imagesRoot);
        return generateImageNames(numberOfQuestions);
    }

    private void generateQuestion(Document document, String imageName, boolean isQuestion, int numberOfQuestion, String imagesRoot) throws MalformedURLException {
        document.add(new AreaBreak());
        String prefix = isQuestion ? "\\q" : "\\a";
        System.out.println(imageName);
        Image question = new Image(ImageDataFactory.create(imagesRoot + prefix + imageName)).setAutoScale(true);
        document.add(new Paragraph("" + numberOfQuestion + ".").setBold().setFontSize(18));
        document.add(question);
        if (isQuestion) {
            document.add(new Paragraph("Write your answer here:").setUnderline().setBold());
            document.add(new AreaBreak());
            document.add(new Paragraph(""));
        }
    }

    private int getNumberOfQuestions(String imagesRoot) {
        List<Integer> questionsNumbers = new LinkedList<>();
        File dir = new File(imagesRoot);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File question : directoryListing) {
                String questionName = question.getName();
                if (!questionName.equals("albert.jpg"))
                    questionsNumbers.add(Integer.parseInt(questionName.substring(1, questionName.length() - 4)));
            }
        }
        return questionsNumbers.stream().max(Integer::compareTo).get();
    }

    private List<String> generateImageNames(int numberOfQuestions) {
        List<String> imageNames = new LinkedList<>();
        Random random = new Random();
        int currentQuestion = (int) (random.nextDouble() * numberOfQuestions) / 4 + 1;
        for (int i = 0; i < 3; i++) {
            imageNames.add(currentQuestion + ".png");
            currentQuestion = (currentQuestion + (int) (random.nextDouble() * 13) + 8) % numberOfQuestions + 1;
        }
        return imageNames;
    }

}