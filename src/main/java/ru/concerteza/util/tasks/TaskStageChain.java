package ru.concerteza.util.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: alexey
 * Date: 5/22/12
 */
public class TaskStageChain implements Serializable {
    private static final long serialVersionUID = 7486673100573364684L;
    private final Map<String, Stage> stageMap;
    private final List<Stage> stageList;

    public TaskStageChain(List<Stage> stageList) {
        this.stageList = stageList;
        ImmutableMap.Builder<String, Stage> bu = ImmutableMap.builder();
        for(Stage ts : stageList) {
            if(!ts.isStart()) bu.put(ts.getIntermediate(), ts);
            bu.put(ts.getCompleted(), ts);
        }
        this.stageMap = bu.build();
    }

    public Stage forName(String stage) {
        Stage res = stageMap.get(stage);
        checkArgument(null != res, "Unknown stage provided, valid stages are: %s", stageList);
        return res;
    }

    public Stage previous(Stage stage) {
        int ind = index(stage);
        checkArgument(0 != ind, "Start stage: '%s', has no previous stage, valid stages are: %s", stage, stageList);
        return stageList.get(ind - 1);
    }

    public Stage next(Stage stage) {
        int ind = index(stage);
        checkArgument(stageList.size() - 1 != ind, "End stage: '%s', has no next stage, valid stages are: %s", stage, stageList);
        return stageList.get(ind + 1);
    }

    public boolean hasNext(Stage stage) {
        return stageList.size() - 1 > index(stage);
    }

    public static Builder builder(String startStage) {
        checkNotNull(startStage, "Null startStage provided");
        return new Builder(startStage);
    }

    private int index(Stage stage) {
        checkNotNull(stage, "Null stage provided");
        // for short lists this should be faster on list than on set
        int pos = stageList.indexOf(stage);
        checkArgument(-1 != pos, "Unknown stage provided, valid stages are: %s", stageList);
        return pos;
    }

    // not threadsafe
    public static class Builder {
        private final ImmutableList.Builder<Stage> builder = ImmutableList.builder();
        private final Set<String> stages = new HashSet<String>();

        private Builder(String startStage) {
            this.builder.add(new Stage(startStage));
            this.stages.add(startStage);
        }

        public Builder add(String intermediate, String completed, String providerId) {
            checkNotNull(intermediate, "Null intermediate stage provided");
            checkNotNull(completed, "Null completed stage provided");
            boolean unique1 = this.stages.add(intermediate);
            checkArgument(unique1, "Duplicate stage provided: '%s'", intermediate);
            boolean unique2 = this.stages.add(completed);
            checkArgument(unique2, "Duplicate stage provided: '%s'", completed);
            this.builder.add(new Stage(intermediate, completed, providerId));
            return this;
        }

        public TaskStageChain build() {
            return new TaskStageChain(builder.build());
        }
    }

    static class Stage implements Serializable {
        private static final long serialVersionUID = 6127466720110180244L;

        private final String intermediate;
        private final String completed;
        private final String processorId;
        private final boolean start;

        Stage(String completed) {
            this.completed = completed;
            this.start = true;
            this.intermediate = null;
            this.processorId = null;
        }

        Stage(String intermediate, String completed, String processorId) {
            this.intermediate = intermediate;
            this.completed = completed;
            this.processorId = processorId;
            this.start = false;
        }

        public String getIntermediate() {
            checkState(!start, "Start stage: '%s' has no intermediate stage", intermediate);
            return intermediate;
        }

        public String getCompleted() {
            return completed;
        }

        public String getProcessorId() {
            checkState(!start, "Start stage: '%s' has no processorId", completed);
            return processorId;
        }

        public boolean isStart() {
            return start;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stage stage = (Stage) o;
            return completed.equals(stage.completed);
        }

        @Override
        public int hashCode() {
            return completed.hashCode();
        }

        @Override
        public String toString() {
            return completed;
        }
    }
}
