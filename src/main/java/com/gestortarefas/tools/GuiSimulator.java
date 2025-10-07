package com.gestortarefas.tools;

import com.gestortarefas.util.RestApiClient;

import java.util.Map;

/**
 * Small CLI tool to simulate GUI behaviour for marking comments as read.
 * Usage: run with one argument: userId (e.g. 13). It will fetch unread counts,
 * pick the first task with unread comments, simulate opening/closing the dialog
 * by calling markCommentsAsRead, and verify the counts.
 */
public class GuiSimulator {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java com.gestortarefas.tools.GuiSimulator <userId>");
            System.exit(1);
        }

        Long userId = Long.parseLong(args[0]);
        RestApiClient client = new RestApiClient();

        System.out.println("Fetching unread counts for userId=" + userId);
        Map<String, Object> unreadResp = client.getUnreadCommentsCount(userId);
        System.out.println("Before: " + unreadResp);

        Map<String, Object> unreadCounts = (Map<String, Object>) unreadResp.get("unreadCounts");
        if (unreadCounts == null || unreadCounts.isEmpty()) {
            System.out.println("No unread comments for user " + userId + ", creating a test comment via API is required.");
            System.exit(0);
        }

        // pick the first task id
        Long taskId = Long.parseLong(unreadCounts.keySet().iterator().next());
        System.out.println("Simulating opening comments for taskId=" + taskId + " and then closing (mark as read)");

        boolean ok = client.markCommentsAsRead(taskId, userId);
        System.out.println("markCommentsAsRead returned: " + ok);

        Map<String, Object> after = client.getUnreadCommentsCount(userId);
        System.out.println("After: " + after);

        System.out.println("Simulation finished.");
    }
}
