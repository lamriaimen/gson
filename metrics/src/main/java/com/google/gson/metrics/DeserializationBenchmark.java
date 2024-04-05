/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.metrics;

import com.google.caliper.BeforeExperiment;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Caliper based micro benchmarks for Gson
 *
 * @author Inderjeet Singh
 * @author Jesse Wilson
 * @author Joel Leitch
 */
public abstract class DeserializationBenchmark {

    protected Gson gson;
    protected String json;

    public static void main(String[] args) {
        NonUploadingCaliperRunner.run(DeserializationBenchmark.class, args);
    }

    @BeforeExperiment
    void setUp() throws Exception {
        this.gson = new Gson();
        this.json = createJson();
    }

    protected abstract String createJson();

    /** Benchmark to measure Gson performance for deserializing an object */
    public void timeDefault(int reps) {
        for (int i = 0; i < reps; ++i) {
            gson.fromJson(json, getTargetClass());
        }
    }

    /** Benchmark to measure deserializing objects by hand */
    public void timeStreaming(int reps) throws IOException, IllegalAccessException {
        for (int i = 0; i < reps; ++i) {
            StringReader reader = new StringReader(json);
            JsonReader jr = new JsonReader(reader);
            deserialize(jr);
        }
    }

    /**
     * This benchmark measures the ideal Gson performance: the cost of parsing a JSON stream and
     * setting object values by reflection. We should strive to reduce the discrepancy between this
     * and {@link #timeDefault(int)}.
     */
    public void timeReflectionStreaming(int reps) throws Exception {
        for (int i = 0; i < reps; ++i) {
            StringReader reader = new StringReader(json);
            JsonReader jr = new JsonReader(reader);
            deserialize(jr);
        }
    }

    protected abstract void deserialize(JsonReader jr) throws IOException, IllegalAccessException;

    protected abstract Class<?> getTargetClass();
}
