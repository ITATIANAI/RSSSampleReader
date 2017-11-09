import javax.xml.stream.XMLStreamException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tanya on 08.11.2017.
 * reads feeds from the internet
 */
public class RSSFeedParser {

    static final String TITLE="title";
    static final String DESCRIPTION="description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";

    final URL url;

    public RSSFeedParser(String feedUrl) {
        try{
            this.url=new URL(feedUrl);
        }catch (MalformedURLException ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public RSSFeed readFeed(){
        RSSFeed feed=null;
        try{
            boolean isFeedHeader=true;
            // Set header  initial values to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory=XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in=read();
            XMLEventReader eventReader=inputFactory.createXMLEventReader(in);
            // read the XML document
            while(eventReader.hasNext()){
                XMLEvent  event=eventReader.nextEvent();
                if (event.isStartElement()){
                    String localPart=event.asStartElement().getName().getLocalPart();
                    switch(localPart){
                        case ITEM:
                            if (isFeedHeader) {
                                isFeedHeader=false;
                                feed=new RSSFeed(title,link,description,language,copyright,pubdate);
                            }
                            event=eventReader.nextEvent();
                            break;
                        case TITLE:
                            title=getCharacterData(event,eventReader);
                            break;
                        case DESCRIPTION:
                            description=getCharacterData(event,eventReader);
                            break;
                        case LINK:
                            link=getCharacterData(event,eventReader);
                            break;
                        case GUID:
                            guid=getCharacterData(event,eventReader);
                            break;
                        case LANGUAGE:
                            language=getCharacterData(event,eventReader);
                            break;
                        case AUTHOR:
                            author=getCharacterData(event,eventReader);
                        case PUB_DATE:
                            pubdate=getCharacterData(event,eventReader);
                            break;
                        case COPYRIGHT:
                            copyright=getCharacterData(event,eventReader);
                            break;
                    }
                }else if (event.isEndElement()){
                    if (event.asEndElement().getName().getLocalPart()==(ITEM)){
                        RSSFeedMessage message=new RSSFeedMessage();
                        message.setAuthor(author);
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setTitle(title);
                        feed.getMessages().add(message);
                        event=eventReader.nextEvent();
                        continue;
                    }
                }
            }
        }catch (XMLStreamException ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return feed;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
        String result="";
        event=eventReader.nextEvent();
        if (event instanceof Characters){
            result=event.asCharacters().getData();
        }
        return result;
    }

    private InputStream read(){
        try{
            return url.openStream();
        }catch (IOException ex){
            ex.printStackTrace();
            throw  new RuntimeException(ex);
        }
    }
}
