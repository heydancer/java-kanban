package manager.history;

import common.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

    static String historyToString(HistoryManager manager) {
        StringBuilder historyIds = new StringBuilder();

        for (Task task : manager.getHistory()) {
            historyIds.append(task.getId()).append(',');
        }

        return historyIds.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] taskArray = value.split(",");

        for (String element : taskArray) {
            historyIds.add(Integer.valueOf(element));
        }

        return historyIds;
    }
}
