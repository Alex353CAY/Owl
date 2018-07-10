package OwlBuilder.XML.StAX;

import OwlBuilder.Phase;
import OwlBuilder.PluginRequest;
import OwlBuilder.Project;
import OwlBuilder.XML.Exceptions.*;
import OwlBuilder.XML.ProjectBuilder;
import owlFramework.Artifact;
import owlFramework.Exceptions.CyclicDependencyDetectedException;
import owlFramework.Plugin.Plugin;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Parser implements OwlBuilder.XML.Parser {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    private XMLEventReader reader;
    private XMLEvent event;
    private ProjectBuilder projectBuilder;
    private final Stack<PluginRequest> plugins = new Stack<>();

    @Override
    public Project parseDocument(String path) throws FileNotFoundException, XMLStreamException, XMLConfigurationParsingException, CyclicDependencyDetectedException, ParserConfigurationException {
        reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
        projectBuilder = new ProjectBuilder();

        if (reader.hasNext()) reader.nextEvent(); //skipping StartDocumentElement
        while (!(event = reader.nextEvent()).isStartElement()) {
            if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) throw new UnexpectedElementException();
        }
        if (!event.asStartElement().getName().toString().equals("project")) throw new UnexpectedElementException();
        while (reader.hasNext()) {
            event = reader.nextEvent();
            //parsingExceptions
            if (event.isEndElement()) break;
            if (event.isEndDocument()) break;
            skipNeedlessInformation();
            if (event.isEndElement()) break;
            if (event.isEndDocument()) break;
            if (!event.isStartElement()) throw new UnexpectedElementException();
            //parsingCompletion

            //processing structure
            skipNeedlessInformation();
            String elementName = event.asStartElement().getName().toString();
            switch (elementName) {
                case "groupId": {
                    projectBuilder.setGroupId(parseElementContent());
                    break;
                }
                case "artifactId": {
                    projectBuilder.setArtifactId(parseElementContent());
                    break;
                }
                case "version": {
                    projectBuilder.setVersion(parseElementContent());
                    break;
                }
                case "plugin": {
                    projectBuilder.addPlugin(parsePlugin());
                    break;
                }
                default: {
                    throw new UnexpectedElementException();
                }
            }
        }
        return projectBuilder.build();
    }

    private void parseProjectsGAV(String option) throws XMLStreamException, UnexpectedElementException {
        assert option != null && (option.equals("groupId") || option.equals("artifactId") || option.equals("version"));
        event = reader.nextEvent();
        if (!event.isCharacters()) throw new UnexpectedElementException();
        while (event.asCharacters().isCData()) {
            event = reader.nextEvent();
            if (!event.isCharacters()) throw new UnexpectedElementException();
        }
        switch (option) {
            case "groupId": {
                projectBuilder.setGroupId(event.asCharacters().getData());
                break;
            }
            case "artifactId": {
                projectBuilder.setArtifactId(event.asCharacters().getData());
                break;
            }
            case "version": {
                projectBuilder.setVersion(event.asCharacters().getData());
                break;
            }
        }
        reader.nextEvent();
    }

    private String parseElementContent() throws XMLStreamException, UnexpectedElementException {
        event = reader.nextEvent();
        if (event.isEndElement()) return "";
        if (!event.isCharacters()) throw new UnexpectedElementException();
        String result = event.asCharacters().getData();
        reader.nextEvent();
        return result;
    }

    private PluginRequest parsePlugin() throws XMLStreamException, UnexpectedElementException, DublicatingElementException, CyclicDependencyDetectedException, ParserConfigurationException {
        String groupId = "", artifactId = "", version = "";
        Map<String, String> configuration = new HashMap<>();
        PluginRequest plugin;
        while (true) {
            event = reader.nextEvent();
            if (event.isEndElement()) break;
            skipNeedlessInformation();
            if (event.isEndElement()) break;
            String elementName = event.asStartElement().getName().toString();
            switch (elementName) {
                case "groupId": {
                    groupId = parseElementContent();
                    break;
                }
                case "artifactId": {
                    artifactId = (parseElementContent());
                    break;
                }
                case "version": {
                    version = parseElementContent();
                    break;
                }
                case "configuration": {
                    configuration = parseConfiguration();
                    break;
                }
                case "dependencies": {
                    if (groupId.isEmpty() || artifactId.isEmpty()) throw new UnexpectedElementException();
                    plugin = new PluginRequest(new Artifact(groupId, artifactId, version), configuration);
                    for (PluginRequest dependency: parseDependencies()) {
                        plugin.addDependency(dependency);
                    }
                    skipNeedlessInformation();
                    event = reader.nextEvent();
                    return plugin;
                }
            }
        }
        return new PluginRequest(new Artifact(groupId, artifactId, version), configuration);
    }

    private Set<PluginRequest> parseDependencies() throws XMLStreamException, UnexpectedElementException, CyclicDependencyDetectedException, DublicatingElementException, ParserConfigurationException {
        final Set<PluginRequest> dependencies = new HashSet<>();
        while (true) {
            if (event.isEndElement()) break;
            skipNeedlessInformation();
            if (event.isEndElement()) break;
            if (event.isStartElement() && event.asStartElement().getName().toString().equals("plugin")) {
                skipNeedlessInformation();
                PluginRequest dependency = parsePlugin();
                dependencies.add(dependency);
            }
            skipNeedlessInformation();
            event = reader.nextEvent();
        }
        return dependencies;
    }

    private Map<String, String> parseConfiguration() throws XMLStreamException, UnexpectedElementException {
        Map<String, String> configuration = new ConcurrentHashMap<>();
        while (true) {
            event = reader.nextEvent();
            skipNeedlessInformation();
            if (event.isEndElement()) break;
            if (event.isStartElement()) {
                configuration.put(event.asStartElement().getName().toString(), parseElementContent());
            }
        }
        //event = reader.nextEvent();
        skipNeedlessInformation();
        return configuration;
    }

    private void skipNeedlessInformation() throws UnexpectedElementException, XMLStreamException {
        while (true) {
            if (event.isCharacters()) {
                if (!event.asCharacters().isWhiteSpace()) throw new UnexpectedElementException();
            } else if (event.getEventType() == XMLStreamConstants.COMMENT) {
            } else break;
            event = reader.nextEvent();
        }
    }
}
