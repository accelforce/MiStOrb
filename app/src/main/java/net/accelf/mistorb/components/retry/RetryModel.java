package net.accelf.mistorb.components.retry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RetryModel {

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    String queue;
    String worker;
    String count;
    Date retryAt;
    String error;

    private RetryModel(String queue, String worker, String count, Date retryAt, String error) {
        this.queue = queue;
        this.worker = worker;
        this.count = count;
        this.retryAt = retryAt;
        this.error = error;
    }

    public static List<RetryModel> toRetries(Document document) {
        Elements rows = document.select("table tbody tr");
        ArrayList<RetryModel> list = new ArrayList<>();
        for (Element element : rows) {
            RetryModel retry = toRetry(element);
            list.add(retry);
        }
        return list;
    }

    private static RetryModel toRetry(Element element) {
        Elements values = element.select("td");

        String dateStr = element.select("td time").first().attr("datetime");
        Date date;
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        return new RetryModel(values.get(3).text(),
                values.get(4).text(),
                values.get(2).text(),
                date,
                values.get(6).text());
    }

}
