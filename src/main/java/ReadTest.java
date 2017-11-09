/**
 * Created by Tanya on 08.11.2017.
 *
 */
public class ReadTest {
    public static void main(String[] args) {
        RSSFeedParser parser = new RSSFeedParser(
                "http://www.wuxiaworld.com/feed/"
               );
               /*  "http://rss.cnn.com/rss/edition.rss"*/
        RSSFeed feed = parser.readFeed();
        System.out.println(feed);
        for (RSSFeedMessage message : feed.getMessages()) {
            System.out.println(message);

        }

    }
}
