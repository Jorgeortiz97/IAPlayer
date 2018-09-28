package com.jorgeortizesc.iaplayer.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jorgeortizesc.iaplayer.domain.Advice;
import com.jorgeortizesc.iaplayer.domain.IAElement;
import com.jorgeortizesc.iaplayer.domain.Input;
import com.jorgeortizesc.iaplayer.domain.Question;

import javafx.util.Duration;

public class XMLReader {


    public static List<IAElement> read(String videoFile) throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException  {

        // Get the XML filename, from the video file.
        String fileName = videoFile;
        if (videoFile.indexOf(".") > 0)
            fileName = videoFile.substring(0, videoFile.lastIndexOf("."));

        fileName = fileName + ".xml";
        File xmlFile = new File(fileName);

        if (!xmlFile.exists())
        	throw new FileNotFoundException("File " + fileName + " doesn't exist.");


        List<IAElement> elements = new LinkedList<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        if (!doc.getDocumentElement().getNodeName().equals("IAPlayer"))
        	throw new IllegalArgumentException("'IAPlayer' tag does not appear in the document.");

        NodeList questions = doc.getElementsByTagName("question");

        // <question>
        for (int q = 0; q < questions.getLength(); q++) {
            Node questionNode = questions.item(q);

            if (questionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element questionElement = (Element) questionNode;

                String titleStr = questionElement.getAttribute("title");
                String rightStr = questionElement.getAttribute("right");
                String timeStr = questionElement.getAttribute("time");

                // Compulsory parameters
                if (titleStr.equals("") || rightStr.equals("") || timeStr.equals(""))
                    throw new IllegalArgumentException("'Title', 'right answer' and 'time' can not be empty fields.");

                int right = 0;
                try {
                    right = Integer.parseInt(rightStr);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Impossible to parse " + rightStr + " into an integer.");
                }

                Duration time = TimeUtil.stringToDuration(timeStr);
                if (time == null)
                	throw new IllegalArgumentException("Impossible to parse " + timeStr + ". The format is 'hh:mm:ss'.");


                String explStr = questionElement.getAttribute("explanation");
                Duration expl = null;
                if (!explStr.equals("")) {
                    expl = TimeUtil.stringToDuration(explStr);
                    if (expl == null)
                    	throw new IllegalArgumentException("Impossible to parse " + explStr + ". The format is 'hh:mm:ss'.");
                    if (expl.greaterThanOrEqualTo(time))
                    	throw new IllegalArgumentException("Explanation time (" + explStr + ") can not be later than the moment of the question (" + timeStr + ").");
                }

                NodeList answers = questionElement.getElementsByTagName("answer");

                if (answers.getLength() == 0)
                	throw new IllegalArgumentException("Question '" + titleStr + "' can not have 0 possible answers.");

                if (right > answers.getLength())
                	throw new IllegalArgumentException("Question '" + titleStr + "' has '" + right + "' as right index answer. But it only has " + answers.getLength() + " answers.");

                String[] answersContainer = new String[answers.getLength()];


                // <answer>
                for (int a = 0; a < answers.getLength(); a++) {
                    Node answerNode = answers.item(a);
                    if (answerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element answerElement = (Element) answerNode;
                        String answerStr = answerElement.getAttribute("content");
                        if (answerStr.equals(""))
                        	 throw new IllegalArgumentException("'Content' can not be an empty field.");
                        answersContainer[a] = answerStr;
                    }
                } // </answer>

                IAElement question = (IAElement) new Question(titleStr, right, time, expl, answersContainer);
                elements.add(question);
            }
        } // </question>


        NodeList inputs = doc.getElementsByTagName("input");

        // <input>
        for (int i = 0; i < inputs.getLength(); i++) {
            Node inputNode = inputs.item(i);

            if (inputNode.getNodeType() == Node.ELEMENT_NODE) {
                Element inputElement = (Element) inputNode;

                String titleStr = inputElement.getAttribute("title");
                String timeStr = inputElement.getAttribute("time");

                // Compulsory parameters
                if (titleStr.equals("") || timeStr.equals(""))
                	throw new IllegalArgumentException("'Title' and 'time' can not be empty fields.");

                Duration time = TimeUtil.stringToDuration(timeStr);
                if (time == null)
                	throw new IllegalArgumentException("Impossible to parse " + timeStr + ". The format is 'hh:mm:ss'.");



                String explStr = inputElement.getAttribute("explanation");
                Duration expl = null;
                if (!explStr.equals("")) {
                    expl = TimeUtil.stringToDuration(explStr);
                    if (expl == null)
                    	throw new IllegalArgumentException("Impossible to parse " + explStr + ". The format is 'hh:mm:ss'.");;
                    if (expl.greaterThanOrEqualTo(time))
                        throw new IllegalArgumentException("Explanation time (" + explStr + ") can not be later than the moment of the input (" + timeStr + ").");
                }

                NodeList answers = inputElement.getElementsByTagName("answer");

                if (answers.getLength() == 0)
                	throw new IllegalArgumentException("Input '" + titleStr + "' can not have 0 right answers.");


                String[] answersContainer = new String[answers.getLength()];

                // <answer>
                for (int a = 0; a < answers.getLength(); a++) {
                    Node answerNode = answers.item(a);
                    if (answerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element answerElement = (Element) answerNode;
                        String answerStr = answerElement.getAttribute("content");
                        if (answerStr.equals(""))
                        	throw new IllegalArgumentException("'Content' can not be an empty field");
                        answersContainer[a] = answerStr;
                    }
                } // </answer>

                IAElement input = (IAElement) new Input(titleStr, time, expl, answersContainer);
                elements.add((IAElement) input);
            }
        } // </input>


        NodeList advices = doc.getElementsByTagName("advice");

        // <advice>
        for (int a = 0; a < advices.getLength(); a++) {
            Node adviceNode = advices.item(a);

            if (adviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element adviceElement = (Element) adviceNode;

                String titleStr = adviceElement.getAttribute("title");
                String timeStr = adviceElement.getAttribute("time");
                String contentStr = adviceElement.getAttribute("content");

                // Compulsory parameters
                if (titleStr.equals("") || timeStr.equals("") || contentStr.equals(""))
                	throw new IllegalArgumentException("'Title', 'time' and 'content' can not be empty fields.");

                Duration time = TimeUtil.stringToDuration(timeStr);
                if (time == null)
                	throw new IllegalArgumentException("Impossible to parse " + timeStr + ". The format is 'hh:mm:ss'.");

                String explStr = adviceElement.getAttribute("explanation");
                Duration expl = null;
                if (!explStr.equals("")) {
                    expl = TimeUtil.stringToDuration(explStr);
                    if (expl == null)
                    	throw new IllegalArgumentException("Impossible to parse " + explStr + ". The format is 'hh:mm:ss'.");
                    if (expl.greaterThanOrEqualTo(time))
                    	throw new IllegalArgumentException("Explanation time (" + explStr + ") can not be later than the moment of the advice (" + timeStr + ").");
                }

                IAElement advice = (IAElement) new Advice(titleStr, time, expl, contentStr);
                elements.add((IAElement) advice);
            }
        } // </input>

        return elements;
    }
}
