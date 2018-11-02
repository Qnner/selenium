// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.grid.web;

import org.openqa.selenium.injector.Injector;
import org.openqa.selenium.remote.http.HttpRequest;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class PredicatedRoute extends Route<PredicatedRoute> {

  private Function<Injector, CommandHandler> handlerFunc;
  private final Predicate<HttpRequest> predicate;

  PredicatedRoute(Predicate<HttpRequest> predicate) {
    this.predicate = Objects.requireNonNull(predicate);
  }

  public PredicatedRoute using(Class<? extends CommandHandler> handlerClass) {
    Objects.requireNonNull(handlerClass);
    handlerFunc = (inj) -> inj.newInstance(handlerClass);
    return this;
  }

  public PredicatedRoute using(CommandHandler handlerInstance) {
    Objects.requireNonNull(handlerInstance);
    handlerFunc = (inj) -> handlerInstance;
    return this;
  }

  @Override
  protected void validate() {
    if (handlerFunc == null) {
      throw new IllegalStateException("Handler for route is required");
    }
  }

  @Override
  protected CommandHandler newHandler(Injector injector, HttpRequest request) {
    if (!predicate.test(request)) {
      return getFallback(injector);
    }

    return handlerFunc.apply(injector);
  }
}
