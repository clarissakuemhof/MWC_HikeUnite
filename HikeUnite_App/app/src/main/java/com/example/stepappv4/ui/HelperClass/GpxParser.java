package com.example.stepappv4.ui.HelperClass;

import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GpxParser {

    public List<GeoPoint> parseGpxFile(InputStream inputStream) {
        List<GeoPoint> geoPoints = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            doc.getDocumentElement().normalize();

            NodeList trksegList = doc.getElementsByTagName("trkseg");

            for (int i = 0; i < trksegList.getLength(); i++) {
                Node trksegNode = trksegList.item(i);

                if (trksegNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element trksegElement = (Element) trksegNode;
                    NodeList trkptList = trksegElement.getElementsByTagName("trkpt");

                    for (int j = 0; j < trkptList.getLength(); j++) {
                        Node trkptNode = trkptList.item(j);

                        if (trkptNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element trkptElement = (Element) trkptNode;

                            double latitude = Double.parseDouble(trkptElement.getAttribute("lat"));
                            double longitude = Double.parseDouble(trkptElement.getAttribute("lon"));
                            double elevation = parseElevation(trkptElement);

                            GeoPoint geoPoint = new GeoPoint(latitude, longitude, elevation);
                            geoPoints.add(geoPoint);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return geoPoints;
    }


    private double parseElevation(Element trkptElement) {
        NodeList eleList = trkptElement.getElementsByTagName("ele");
        if (eleList.getLength() > 0) {
            Node eleNode = eleList.item(0);
            return Double.parseDouble(eleNode.getTextContent());
        }
        return 0.0; // Default elevation if not found
    }


}