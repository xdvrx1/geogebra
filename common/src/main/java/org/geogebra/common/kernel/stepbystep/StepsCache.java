package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

import java.util.HashMap;
import java.util.Map;

public class StepsCache {

    private static StepsCache instance;

    private Map<StepNode, CacheEntry> regroupCache;
    private Map<StepNode, CacheEntry> expandCache;
    private Map<StepNode, CacheEntry> factorCache;

    private static class CacheEntry {
        private StepNode result;
        private SolutionStep steps;
    }

    private StepsCache() {
        regroupCache = new HashMap<>();
        expandCache = new HashMap<>();
        factorCache = new HashMap<>();
    }

    public StepNode regroup(StepNode sn, SolutionBuilder sb) {
        CacheEntry entry = regroupCache.get(sn);

        if (entry == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            entry = new CacheEntry();

            entry.result = StepStrategies.defaultRegroup(sn, tempSteps);
            entry.steps = tempSteps.getSteps();

            regroupCache.put(sn, entry);
        }

        if (sb != null) {
            sb.addAll(entry.steps);
        }
        return entry.result;
    }

    public StepNode expand(StepNode sn, SolutionBuilder sb) {
        CacheEntry entry = expandCache.get(sn);

        if (entry == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            entry = new CacheEntry();

            entry.result = StepStrategies.defaultExpand(sn, tempSteps);
            entry.steps = tempSteps.getSteps();

            expandCache.put(sn, entry);
        }

        if (sb != null) {
            sb.addAll(entry.steps);
        }
        return entry.result;
    }

    public StepNode factor(StepNode sn, SolutionBuilder sb) {
        CacheEntry entry = factorCache.get(sn);

        if (entry == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            entry = new CacheEntry();

            entry.result = StepStrategies.defaultFactor(sn, tempSteps);
            entry.steps = tempSteps.getSteps();

            factorCache.put(sn, entry);
        }

        if (sb != null) {
            sb.addAll(entry.steps);
        }
        return entry.result;
    }

    public static StepsCache getInstance() {
        if (instance == null) {
            instance = new StepsCache();
        }

        return instance;
    }
}
