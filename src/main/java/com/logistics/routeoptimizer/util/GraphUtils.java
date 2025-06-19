package com.logistics.routeoptimizer.util;

public class GraphUtils {
    private static final int INF = Integer.MAX_VALUE / 2;

    public static int[][] initializeGraph(int[][] graph) {
        int n = graph.length;
        int[][] distances = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distances[i][j] = graph[i][j] == 0 && i != j ? INF : graph[i][j];
            }
        }
        return distances;
    }

    public static void floydWarshall(int[][] distances) {
        int n = distances.length;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distances[i][k] != INF && distances[k][j] != INF && 
                        distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                    }
                }
            }
        }
    }
}