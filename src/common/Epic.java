package common;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds=" + subTaskIds +
                '}';
    }
}
