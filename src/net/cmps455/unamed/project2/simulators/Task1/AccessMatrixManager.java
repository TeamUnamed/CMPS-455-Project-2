package net.cmps455.unamed.project2.simulators.Task1;

import net.cmps455.unamed.project2.api.AccessManager;
import net.cmps455.unamed.project2.api.DomainManager;

public class AccessMatrixManager extends AccessManager {

    private final int[][] matrix;
    private final DomainManager domainManager;
    private final int domainCount;
    private final int fileCount;

    public AccessMatrixManager(DomainManager domainManager, int fileCount) {
        this.domainManager = domainManager;
        this.domainCount = domainManager.getDomainCount();
        this.fileCount = fileCount;

        this.matrix = new int[domainCount][domainCount + fileCount];
    }

    @Override
    public int getDomainCount() {
        return domainCount;
    }

    @Override
    public int getFileCount() {
        return fileCount;
    }

    @Override
    public boolean canRead(int domain, int object) {
        return (matrix[domainManager.getContext(domain)][object] & 0b001) == 0b001;
    }

    @Override
    public boolean canWrite(int domain, int object) {
        return (matrix[domainManager.getContext(domain)][object] & 0b010) == 0b010;
    }

    @Override
    public boolean canSwitch(int domainSource, int domainTarget) {
        return (matrix[domainManager.getContext(domainSource)][domainTarget + fileCount] & 0b100) == 0b100;
    }

    @Override
    public void set(int domain, int object, int value) {
        matrix[domain][object] = value;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("    ");
        for (int i = -1; i < domainCount; i++) {
            if (i < 0) {
                for (int j = 0; j < fileCount; j++) {
                    output.append(" F").append(j + 1).append("  ");
                }
                for (int j = 0; j < domainCount; j++) {
                    output.append(" D").append(j + 1).append("  ");
                }
                output.append("\n");
                System.out.println();
                continue;
            }

            output.append("  D").append(i+1);
            for (int j = 0; j < fileCount; j++) {
                int v = matrix[i][j];
                output.append(String.format(" %-3s ", v == 1 ? "R" : v == 2 ? "W" : v == 3 ? "R/W" : ""));
            }
            for (int j = 0; j < domainCount; j++) {
                output.append(String.format(" %-3s ", matrix[i][fileCount+j] == 4 ? "A" : ""));
            }
            output.append("\n");
        }

        return output.toString();
    }
}
