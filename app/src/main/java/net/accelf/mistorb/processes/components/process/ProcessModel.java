package net.accelf.mistorb.processes.components.process;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProcessModel {

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    Status status;
    String id;
    String directory;
    Date startedAt;
    String queues;
    String running;
    String threads;

    private ProcessModel(Status status, String id, String directory, Date startedAt,
                         String queues, String running, String threads) {
        this.status = status;
        this.id = id;
        this.directory = directory;
        this.startedAt = startedAt;
        this.queues = queues;
        this.running = running;
        this.threads = threads;
    }

    public static List<ProcessModel> toProcesses(Document document) {
        Elements rows = document.select("table.processes tbody tr");
        ArrayList<ProcessModel> list = new ArrayList<>();
        for (Element element : rows) {
            ProcessModel process = toProcess(element);
            list.add(process);
        }
        return list;
    }

    private static ProcessModel toProcess(Element element) {
        Element box = element.select("td.box").first();
        ArrayList<String> boxText = new ArrayList<>(Arrays.asList(box.ownText().split(" ")));
        String id = boxText.get(0);
        boxText.remove(0);

        String dateStr = element.select("td time").first().attr("datetime");
        Date date;
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        Elements values = element.select("td");

        String running = values.get(3).text();
        Status status = Status.RUNNING;
        if (!box.select("span.label-danger").isEmpty()) {
            status = Status.QUIET;
        }
        if (!running.equals("0")) {
            status = Status.BUSY;
        }


        return new ProcessModel(status,
                id,
                box.select("span.label-success").first().text(),
                date,
                StringUtils.join(boxText, " "),
                running,
                values.get(2).text());
    }

    enum Status {
        BUSY,
        RUNNING,
        QUIET
    }

}
