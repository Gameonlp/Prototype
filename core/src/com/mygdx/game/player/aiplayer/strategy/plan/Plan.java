package com.mygdx.game.player.aiplayer.strategy.plan;

import java.util.LinkedList;
import java.util.List;

public class Plan{
    public interface PlanExecutor{
        void execute(Step step);
    }
    private static class ConditionalTree{
        private final Condition condition;
        private final Step step;
        private List<ConditionalTree> subTrees;

        public ConditionalTree(Condition condition, Step step, List<ConditionalTree> subTrees){
            this.condition = condition;
            this.step = step;
            this.subTrees = subTrees;
        }

        public ConditionalTree matchingSubTree(){
            if (subTrees != null) {
                for (ConditionalTree subTree : subTrees) {
                    if (subTree != null && subTree.condition.applies()){
                        return subTree;
                    }
                }
            }
            return null;
        }

        public String prettyPrint(){
            return this.prettyPrint(0);
        }

        public String prettyPrint(int indentLevel){
            StringBuilder out = new StringBuilder();
            StringBuilder indent = new StringBuilder();
            for (int i = 0; i < indentLevel; i++) {
                indent.append("\t");
            }
            out.append(indent);
            out.append("ConditionalTree{" + "condition=").append(condition).append(", step=").append(step).append(", subTrees=\n");
            for (ConditionalTree subTree : subTrees) {
                if (subTree != null) {
                    out.append(subTree.prettyPrint(indentLevel + 1));
                } else {
                    out.append(indent);
                    out.append("\t");
                    out.append("null");
                }
                out.append("\n");
            }
            out.append(indent);
            out.append("}");
            return String.valueOf(out);
        }

        @Override
        public String toString() {
            return "ConditionalTree{" +
                    "condition=" + condition +
                    ", step=" + step +
                    ", subTrees=" + subTrees +
                    '}';
        }
    }

    ConditionalTree planTree;

    public Plan(){
        this.planTree = null;
    }

    public void setStep(Condition condition, Step step){
        this.planTree = new ConditionalTree(condition, step, null);
    }

    public void setStep(Step step){
        this.setStep(() -> true, step);
    }

    public void addSubPlans(List<Plan> subPlans){
        List<ConditionalTree> list = new LinkedList<>();
        for (Plan x : subPlans) {
            ConditionalTree tree = x.planTree;
            list.add(tree);
        }
        planTree.subTrees = list;
    }

    public void evaluateConditions(){
        planTree = planTree.matchingSubTree();
    }

    public boolean executeNext(PlanExecutor executor){
        if (planTree != null){
            executor.execute(planTree.step);
            //planTree = planTree.matchingSubTree();
        }
        return planTree != null;
    }

    public String prettyPrint(){
        return "Plan{" +
                "planTree=" + planTree.prettyPrint() +
                '}';
    }

    @Override
    public String toString() {
        return "Plan{" +
                "planTree=" + planTree +
                '}';
    }
}
