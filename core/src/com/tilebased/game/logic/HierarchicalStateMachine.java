package com.tilebased.game.logic;

import com.tilebased.game.SettingsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HierarchicalStateMachine {

    private HierarchicalStateMachine current;
    private final TransitionFunction transitionFunction;
    private HierarchicalStateMachine start;
    private final boolean resets;
    private String name;

    private class TransitionFunction {
        private class Element{
            private Guard guard;
            private Action action;
            private HierarchicalStateMachine state;

            private Element(Guard guard, Action action, HierarchicalStateMachine state) {
                this.guard = guard;
                this.action = action;
                this.state = state;
            }

            public Guard getGuard() {
                return guard;
            }

            public Action getAction() {
                return action;
            }

            public HierarchicalStateMachine getState() {
                return state;
            }

            @Override
            public String toString() {
                return "Element{" +
                        "guard=" + guard +
                        ", action=" + action +
                        ", state=" + state +
                        '}';
            }
        }

        private class Input{
            private HierarchicalStateMachine state;
            private String input;

            private Input(HierarchicalStateMachine state, String input) {
                this.state = state;
                this.input = input;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Input input1 = (Input) o;
                return state.equals(input1.state) && input.equals(input1.input);
            }

            @Override
            public int hashCode() {
                return Objects.hash(state, input);
            }

            @Override
            public String toString() {
                return "Input{" +
                        "state=" + state +
                        ", input='" + input + '\'' +
                        '}';
            }
        }

        Map<Input, Element> transitionFunction;

        public TransitionFunction(){
            transitionFunction = new HashMap<>();
        }

        public void addTransition(HierarchicalStateMachine source, String input, Guard guard, Action action, HierarchicalStateMachine goal){
            transitionFunction.put(new Input(source, input), new Element(guard, action, goal));
        }

        public Element getNext(HierarchicalStateMachine current, String input){
            return transitionFunction.get(new Input(current, input));
        }

        @Override
        public String toString() {
            return "TransitionFunction{" +
                    "transitionFunction=" + transitionFunction +
                    '}';
        }
    }

    public void setStart(HierarchicalStateMachine start){
        this.start = start;
        this.current = start;
        if (!states.containsKey(start.name)){
            states.put(start.name, start);
        }
    }

    public interface Guard{
        Guard nothing = () -> true;

        boolean call();
    }

    public interface Action{
        Action nothing = () -> {};

        void call();
    }

    private Map<String, HierarchicalStateMachine> states;

    public HierarchicalStateMachine(String name) {
        this(true, name);
    }

    public HierarchicalStateMachine(boolean resets, String name){
        this.resets = resets;
        this.name = name;
        states = new HashMap<>();
        transitionFunction = new TransitionFunction();
    }

    public HierarchicalStateMachine getStateByName(String name){
        return states.get(name);
    }

    public HierarchicalStateMachine getCurrent() {
        return current;
    }

    public void transition(String input){
        TransitionFunction.Element next = this.transitionFunction.getNext(current, input);
        if(next != null){
            if(next.getGuard().call()){
                next.getAction().call();
                current.leaveState();
                current = next.getState();
            } else if (current != null) {
                current.transition(input);
            }
        } else if (current != null){
            current.transition(input);
        }
        if (SettingsManager.getInstance().getBooleanSetting("DebugPrintStates")) {
            System.out.println(current);
        }
    }

    private void leaveState() {
        if(resets) {
            current = start;
        }
    }

    public void addTransition(HierarchicalStateMachine source, String input, Action action, HierarchicalStateMachine goal) {
        addTransition(source, input, Guard.nothing, action, goal);
    }

    public void addTransition(HierarchicalStateMachine source, String input, HierarchicalStateMachine goal) {
        addTransition(source, input, Guard.nothing, goal);
    }

    public void addTransition(HierarchicalStateMachine source, String input, Guard guard, HierarchicalStateMachine goal) {
        addTransition(source, input, guard, Action.nothing, goal);
    }

    public void addTransition(HierarchicalStateMachine source, String input, Guard guard, Action action, HierarchicalStateMachine goal){
        if (!states.containsKey(source.name)){
            states.put(source.name, source);
        }
        if (!states.containsKey(goal.name)){
            states.put(goal.name, goal);
        }
        transitionFunction.addTransition(source, input, guard, action, goal);
    }

    @Override
    public String toString() {
        return "HierarchicalStateMachine{" +
                "current=" + current +
                ", transitionFunction=" + transitionFunction +
                ", start=" + start +
                ", resets=" + resets +
                '}';
    }
}

