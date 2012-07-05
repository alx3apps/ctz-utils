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
 * Implementation of task stages list. Must be provided by {@link Task} instances. Thread-safe.
 *
 * @author alexey
 * Date: 5/22/12
 * @see TaskEngine
 * @see Task
 * @see TaskProcessorProvider
 */
public class TaskStageChain implements Serializable {
    private static final long serialVersionUID = 7486673100573364684L;
    private final Map<String, Stage> stageMap;
    private final List<Stage> stageList;

    private TaskStageChain(List<Stage> stageList) {
        this.stageList = stageList;
        ImmutableMap.Builder<String, Stage> bu = ImmutableMap.builder();
        for(Stage ts : stageList) {
            if(!ts.isStart()) bu.put(ts.getIntermediate(), ts);
            bu.put(ts.getCompleted(), ts);
        }
        this.stageMap = bu.build();
    }

    /**
     * @param stage stage name
     * @return {@link Stage} instance for given name
     */
    public Stage forName(String stage) {
        checkNotNull(stage, "Null stage provided");
        Stage res = stageMap.get(stage);
        checkArgument(null != res, "Unknown stage provided, valid stages are: %s", stageList);
        return res;
    }

    /**
     * @param stage input stage
     * @return previous stage
     */
    public Stage previous(Stage stage) {
        int ind = index(stage);
        checkArgument(0 != ind, "Start stage: '%s', has no previous stage, valid stages are: %s", stage, stageList);
        return stageList.get(ind - 1);
    }

    /**
     * @param stage input stage
     * @return next stage
     */
    public Stage next(Stage stage) {
        int ind = index(stage);
        checkArgument(stageList.size() - 1 != ind, "End stage: '%s', has no next stage, valid stages are: %s", stage, stageList);
        return stageList.get(ind + 1);
    }

    /**
     * @param stage input stage
     * @return whether next stage exists for given stage
     */
    public boolean hasNext(Stage stage) {
        return stageList.size() - 1 > index(stage);
    }

    /**
     * @param startStage start stage
     * @return {@link Builder} builder for chain
     */
    public static Builder builder(Enum<?> startStage) {
        checkNotNull(startStage, "Null startStage provided");
        return builder(startStage.name());
    }

    /**
     * @param startStage start stage name
     * @return {@link Builder} builder for chain
     */
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


    /**
     * Builder class for {@link TaskStageChain}, not thread-safe
     */
    public static class Builder {
        private final ImmutableList.Builder<Stage> builder = ImmutableList.builder();
        private final Set<String> stages = new HashSet<String>();

        private Builder(String startStage) {
            this.builder.add(new Stage(startStage));
            this.stages.add(startStage);
        }

        /**
         * Adds new enum stage to chain
         *
         * @param intermediate intermediate stage, e.g. 'running', 'loading_data'
         * @param completed completed stage, e.g. 'finished', 'data_loaded'
         * @param processorId id of the processor that will be used for this stage
         * @return builder instance
         */
        public Builder add(Enum<?> intermediate, Enum<?> completed, String processorId) {
            checkNotNull(intermediate, "Null intermediate stage provided");
            checkNotNull(completed, "Null completed stage provided");
            return add(intermediate.name(), completed.name(), processorId);
        }

        /**
         * Adds new stage to chain
         *
         * @param intermediate intermediate stage name, e.g. 'running', 'loading_data'
         * @param completed completed stage name, e.g. 'finished', 'data_loaded'
         * @param processorId id of the processor that will be used for this stage
         * @return builder instance
         */
        public Builder add(String intermediate, String completed, String processorId) {
            checkNotNull(intermediate, "Null intermediate stage provided");
            checkNotNull(completed, "Null completed stage provided");
            boolean unique1 = this.stages.add(intermediate);
            checkArgument(unique1, "Duplicate stage provided: '%s'", intermediate);
            boolean unique2 = this.stages.add(completed);
            checkArgument(unique2, "Duplicate stage provided: '%s'", completed);
            this.builder.add(new Stage(intermediate, completed, processorId));
            return this;
        }

        /**
         * @return stage chain instance
         */
        public TaskStageChain build() {
            return new TaskStageChain(builder.build());
        }
    }

    /**
     * Inner implementation of stage
     */
    protected static class Stage implements Serializable {
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
