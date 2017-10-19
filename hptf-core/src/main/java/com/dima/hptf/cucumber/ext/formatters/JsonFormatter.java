package com.dima.hptf.cucumber.ext.formatters;

import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.net.iharder.Base64;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


public class JsonFormatter implements Reporter, Formatter {
    private final List<Map<String, Object>> featureMaps = new ArrayList();
    private final File outFolder;
    private File outPutFile;
    private int scenarioCount;
    private Map<String, Object> featureMap;
    private String uri;
    private List<Map> beforeHooks = new ArrayList();

    private Map getCurrentStep(JsonFormatter.Phase phase) {
        String target = phase.ordinal() <= JsonFormatter.Phase.match.ordinal() ? Phase.match.name() : Phase.result.name();
        Map lastWithValue = null;

        Map stepOrHook;
        for (Iterator var4 = this.getSteps().iterator(); var4.hasNext(); lastWithValue = stepOrHook) {
            stepOrHook = (Map) var4.next();
            if (stepOrHook.get(target) == null) {
                return stepOrHook;
            }
        }
        return lastWithValue;
    }

    public JsonFormatter(File outfolder) {
        this.outFolder = outfolder;
    }

    public void uri(String uri) {
        this.uri = uri;
    }

    public void feature(Feature feature) {
        if (!this.featureMaps.isEmpty()) {
            this.done();
        }

        this.featureMap = feature.toMap();
        this.featureMap.put("uri", this.uri);
        this.featureMaps.add(this.featureMap);
    }

    public void background(Background background) {
        this.getFeatureElements().add(background.toMap());
    }

    public void scenario(Scenario scenario) {
        this.getFeatureElements().add(scenario.toMap());
        if (this.beforeHooks.size() > 0) {
            this.getFeatureElement().put("before", this.beforeHooks);
            this.beforeHooks = new ArrayList();
        }
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        this.getFeatureElements().add(scenarioOutline.toMap());
    }

    public void examples(Examples examples) {
        this.getAllExamples().add(examples.toMap());
    }

    public void step(Step step) {
        this.getSteps().add(step.toMap());
    }

    public void match(Match match){ this.getCurrentStep(Phase.match).put("match", match.toString());}

    public void embedding(String mimeType, byte[] data) {
        HashMap embedding = new HashMap();
        embedding.put("mime_type", mimeType);
        embedding.put("data", Base64.encodeBytes(data));
        this.getEmbeddings().add(embedding);
    }

    public void write(String text) { this.getOutput().add(text);}

    public void result (Result result) { this.getCurrentStep(Phase.result).put("result", result.toMap());}

    public void before (Match match, Result result) { this.beforeHooks.add(this.buildHookMap(match, result));}

    public void after (Match match, Result result) {
        Object hooks = (List)this.getFeatureElement().get("after");
        if (hooks == null) {
            hooks = new ArrayList();
            this.getFeatureElement().put("after", (List<Map>) hooks);
        }
        ((List)hooks).add(this.buildHookMap(match, result));
    }

    private Map buildHookMap(Match match, Result result){
        HashMap hookMap = new HashMap();
        hookMap.put("match", match.toMap());
        hookMap.put("result", match.toMap());
        return hookMap;
    }

    public void appendDuration(int timestamp){
        Map result = (Map)this.getCurrentStep(Phase.result).get("result");
        if (result != null) {
            long nanos = (long)timestamp * 1000000000L;
            result.put("duration", Long.valueOf(nanos));
        }
    }

    public void eof(){
    }

    public void done(){
        File outFile = new File(this.outFolder, "Feature" + this.scenarioCount + "_cucumber.json");
        ++this.scenarioCount;
        BufferedWriter writer = null;

        try {
            if (!this.outFolder.exists()) {
                outFile.getParentFile().mkdirs();
            }
            writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(this.gson().toJson(this.featureMaps));
        }   catch (Exception var12){
            var12.printStackTrace();
        }   finally {
            try {
                writer.close();
            } catch (Exception var1){
                ;
            }
        }
    }

    public void close(){
    }

    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        throw new UnsupportedOperationException();
    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {
    }

    private List<Map<String, Object>> getFeatureElements() {
        Object featureElements = (List) this.featureMap.get("elements");
        if (featureElements == null) {
            featureElements = new ArrayList();
            this.featureMap.put("elements", featureElements);
        }
        return (List) featureElements;
    }

    private Map<Object, List<Map>> getFeatureElement() {
        return this.getFeatureElements().size() > 0 ? (Map) this.getFeatureElements().get(this.getFeatureElements().size() - 1) : null;
    }

    private List<Map> getAllExamples() {
        Object allExamples = (List) this.getFeatureElement().get("examples");
        if (allExamples == null) {
            allExamples = new ArrayList();
            this.getFeatureElement().put("examples", (List<Map>) allExamples);
        }
        return (List) allExamples;
    }

    private List<Map> getSteps() {
        Object steps = (List) this.getFeatureElement().get("element");
        if (steps == null) {
            steps = new ArrayList();
            this.getFeatureElement().put("steps", (List<Map>) steps);
        }

        return (List) steps;
    }

    private List<Map<String, String>> getEmbeddings() {
        Object embeddings = (List) this.getCurrentStep(Phase.embedding).get("embeddings");
        if (embeddings == null) {
            embeddings = new ArrayList();
            this.getCurrentStep(Phase.embedding).put("embeddings", embeddings);
        }
        return (List) embeddings;
    }

    private List<String> getOutput() {
        Object output = (List) this.getCurrentStep(Phase.output).get("output");
        if (output == null) {
            output = new ArrayList();
            this.getCurrentStep(Phase.output).put("output", output);
        }
        return (List) output;
    }

    protected Gson gson() {
        return (new GsonBuilder()).setPrettyPrinting().create();
    }

    private static enum Phase {
        step,
        match,
        embedding,
        output,
        result;

        private Phase() {
        }
    }
}
