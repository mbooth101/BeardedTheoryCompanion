package uk.co.matbooth.beardedtheory.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import uk.co.matbooth.beardedtheory.model.Event;
import uk.co.matbooth.beardedtheory.model.Performer;
import uk.co.matbooth.beardedtheory.model.Stage;

/**
 * A parser that will extract lists of events, days, stages and performers from
 * a "Clash Finder" style XML stream.
 * <p/>
 * Example usage:
 * <p/>
 * <pre>
 * ClashFinderParser parser = new ClashFinderParser();
 * parser.parse(new InputStream(...));
 * List<Event> events = parser.getEvents();
 * </pre>
 *
 * @see <a href="http://clashfinder.com/">http://clashfinder.com/</a> for more
 * information about Clash Finder
 */
public class ClashFinderParser extends DefaultHandler {
    private static final SAXParserFactory SPF = SAXParserFactory.newInstance();

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat EVT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private final Stack<String> elementStack = new Stack<>();

    // Data parsed out of the clash finder data
    private final List<Event> events = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();
    private final List<Performer> performers = new ArrayList<>();

    private Event current;
    private int eventId = 0;
    private String partial;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementStack.push(qName);
        if ("event".equals(qName)) {
            current = new Event();
            current.setId(eventId++);
            // Stage will already be known when we encounter an event, it's
            // the last one we encountered
            current.setStage(stages.get(stages.size() - 1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        elementStack.pop();
        if ("event".equals(qName)) {
            events.add(current);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String data = new String(ch, start, length);
        if (partial != null) {
            data = partial + data;
        }
        if ("name".equals(peek(1)) && "location".equals(peek(2))) {
            Stage stage = new Stage();
            stage.setName(data.trim());
            if (!stages.contains(stage)) {
                stages.add(stage);
            }
        }
        if ("name".equals(peek(1)) && "event".equals(peek(2))) {
            Performer performer = new Performer();
            performer.setName(data.trim());
            if (!performers.contains(performer)) {
                performers.add(performer);
            }
            current.setPerformer(performer);
        }
        if ("start".equals(peek(1)) && "event".equals(peek(2))) {
            try {
                Date time = EVT_FORMAT.parse(data);
                current.setStartTime(time);

                // Figure out the day from the start time
                Date day = DAY_FORMAT.parse(data);
                current.setDay(day);
                partial = null;
            } catch (ParseException e) {
                // Could have only partial data, so allow it try again one more time
                if (partial == null) {
                    partial = data;
                } else {
                    partial = null;
                    throw new SAXException("Unable to parse event start time", e);
                }
            }
        }
        if ("end".equals(peek(1)) && "event".equals(peek(2))) {
            try {
                Date time = EVT_FORMAT.parse(data);
                current.setEndTime(time);
                partial = null;
            } catch (ParseException e) {
                // Could have only partial data, so allow it try again one more time
                if (partial == null) {
                    partial = data;
                } else {
                    partial = null;
                    throw new SAXException("Unable to parse event end time", e);
                }
            }
        }
    }

    @Nullable
    private String peek(int n) {
        if (elementStack.size() >= n) {
            return elementStack.get(elementStack.size() - n);
        } else {
            return null;
        }
    }

    public void parse(@NonNull InputStream in) throws ParserConfigurationException, SAXException, IOException {
        if (eventId != 0) {
            throw new IllegalStateException("parse was already called");
        }
        SPF.setNamespaceAware(true);
        SAXParser parser = SPF.newSAXParser();
        parser.parse(in, this);
    }

    @NonNull
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @NonNull
    public List<Stage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    @NonNull
    public List<Performer> getPerformers() {
        return Collections.unmodifiableList(performers);
    }
}