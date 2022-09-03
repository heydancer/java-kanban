package manager.history;

import common.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private final CustomLinkedList historyList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        remove(task.getId());

        if (nodeMap.size() == 10) {
            remove(historyList.first.task.getId());
        }

        historyList.linkLast(task);
        nodeMap.put(task.getId(), historyList.last);
    }

    @Override
    public void remove(int id) {
        Node deletedNode;

        if (nodeMap.containsKey(id)) {
            deletedNode = nodeMap.remove(id);
            historyList.removeNode(deletedNode);
        }
    }

    @Override
    public List<Task> getHistory() {

        return historyList.getTasks();
    }

    private static class CustomLinkedList {
        private Node first;
        private Node last;

        private void linkLast(Task task) {
            Node currentNode = new Node(last, task, null);

            if (first == null) {
                first = currentNode;
            } else {
                last.next = currentNode;
            }
            last = currentNode;
        }

        private void removeNode(Node node) {
            if (node == null) {
                return;
            }

            if (node.prev != null) {
                node.prev.next = node.next;

                if (node.next == null) {
                    last = node.prev;
                } else {
                    node.next.prev = node.prev;
                }
            } else {
                first = node.next;

                if (first == null) {
                    last = null;
                } else {
                    first.prev = null;
                }
            }
        }

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();

            Node currentNode = first;

            while (currentNode != null) {
                tasks.add(currentNode.task);
                currentNode = currentNode.next;
            }
            return tasks;
        }
    }
}

