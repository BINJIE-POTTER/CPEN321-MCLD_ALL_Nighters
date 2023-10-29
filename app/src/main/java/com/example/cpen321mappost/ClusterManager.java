package com.example.cpen321mappost;

public class ClusterManager {
    private static ClusterManager instance;
    private Cluster[] allClusters;

    private ClusterManager() {}

    public static ClusterManager getInstance() {
        if (instance == null) {
            instance = new ClusterManager();
        }
        return instance;
    }

    public void setAllClusters(Cluster[] clusters) {
        this.allClusters = clusters;
    }

    public Cluster[] getAllClusters() {
        return allClusters;
    }
}
