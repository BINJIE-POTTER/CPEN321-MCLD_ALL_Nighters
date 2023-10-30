package com.example.cpen321mappost;

public class ClusterManager {
    private static ClusterManager instance;
    private Cluster[] allClusters;

    private ClusterManager() {}

    //ChatGPT usage: No
    public static ClusterManager getInstance() {
        if (instance == null) {
            instance = new ClusterManager();
        }
        return instance;
    }

    //ChatGPT usage: No
    public void setAllClusters(Cluster[] clusters) {
        this.allClusters = clusters;
    }

    //ChatGPT usage: No
    public Cluster[] getAllClusters() {
        return allClusters;
    }
}
