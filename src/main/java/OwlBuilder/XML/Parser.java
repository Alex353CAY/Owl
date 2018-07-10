package OwlBuilder.XML;

import OwlBuilder.Project;
import OwlBuilder.XML.Exceptions.*;
import owlFramework.Exceptions.CyclicDependencyDetectedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;

public interface Parser {
    Project parseDocument(String path) throws FileNotFoundException, XMLStreamException, XMLConfigurationParsingException, CyclicDependencyDetectedException, ParserConfigurationException;
}
