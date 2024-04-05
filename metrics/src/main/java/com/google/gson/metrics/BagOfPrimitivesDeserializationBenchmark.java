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
import java.lang.reflect.Field;

/**
 * Caliper based micro benchmarks for Gson
 *
 * @author Inderjeet Singh
 * @author Jesse Wilson
 * @author Joel Leitch
 */
public class BagOfPrimitivesDeserializationBenchmark extends DeserializationBenchmark {

  @Override
  protected String createJson() {
    BagOfPrimitives bag = new BagOfPrimitives(10L, 1, false, "foo");
    return gson.toJson(bag);
  }

  @Override
  protected void deserialize(JsonReader jr) throws IOException, IllegalAccessException {
    jr.beginObject();
    BagOfPrimitives bag = new BagOfPrimitives();
    while (jr.hasNext()) {
      String name = jr.nextName();
      for (Field field : BagOfPrimitives.class.getDeclaredFields()) {
        if (field.getName().equals(name)) {
          Class<?> fieldType = field.getType();
          if (fieldType.equals(long.class)) {
            field.setLong(bag, jr.nextLong());
          } else if (fieldType.equals(int.class)) {
            field.setInt(bag, jr.nextInt());
          } else if (fieldType.equals(boolean.class)) {
            field.setBoolean(bag, jr.nextBoolean());
          } else if (fieldType.equals(String.class)) {
            field.set(bag, jr.nextString());
          } else {
            throw new RuntimeException("Unexpected: type: " + fieldType + ", name: " + name);
          }
        }
      }
    }
    jr.endObject();
  }

  @Override
  protected Class<?> getTargetClass() {
    return BagOfPrimitives.class;
  }
}