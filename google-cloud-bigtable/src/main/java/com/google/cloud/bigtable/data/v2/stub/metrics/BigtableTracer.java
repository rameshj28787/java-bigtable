/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.stub.metrics;

import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.tracing.ApiTracer;
import com.google.api.gax.tracing.BaseApiTracer;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.Duration;

/**
 * A Bigtable specific {@link ApiTracer} that will be used to plumb additional context through the
 * call chains as well as combines multiple user defined {@link ApiTracer}s into a single one. This
 * will ensure that operation lifecycle events are plumbed through while maintaining user configured
 * functionalities.
 */
class BigtableTracer extends BaseApiTracer {
  private final List<ApiTracer> children;
  private volatile int attempt = 0;

  BigtableTracer(List<ApiTracer> children) {
    this.children = ImmutableList.copyOf(children);
  }

  @Override
  public Scope inScope() {
    final List<Scope> childScopes = new ArrayList<>(children.size());

    for (ApiTracer child : children) {
      childScopes.add(child.inScope());
    }

    return new Scope() {
      @Override
      public void close() {
        for (Scope childScope : childScopes) {
          childScope.close();
        }
      }
    };
  }

  @Override
  public void operationSucceeded() {
    for (ApiTracer child : children) {
      child.operationSucceeded();
    }
  }

  @Override
  public void operationCancelled() {
    for (ApiTracer child : children) {
      child.operationCancelled();
    }
  }

  @Override
  public void operationFailed(Throwable error) {
    for (ApiTracer child : children) {
      child.operationFailed(error);
    }
  }

  @Override
  public void connectionSelected(String id) {
    for (ApiTracer child : children) {
      child.connectionSelected(id);
    }
  }

  @Override
  public void attemptStarted(int attemptNumber) {
    this.attempt = attemptNumber;
    for (ApiTracer child : children) {
      child.attemptStarted(attemptNumber);
    }
  }

  @Override
  public void attemptSucceeded() {
    for (ApiTracer child : children) {
      child.attemptSucceeded();
    }
  }

  @Override
  public void attemptCancelled() {
    for (ApiTracer child : children) {
      child.attemptCancelled();
    }
  }

  @Override
  public void attemptFailed(Throwable error, Duration delay) {
    for (ApiTracer child : children) {
      child.attemptFailed(error, delay);
    }
  }

  @Override
  public void attemptFailedRetriesExhausted(Throwable error) {
    for (ApiTracer child : children) {
      child.attemptFailedRetriesExhausted(error);
    }
  }

  @Override
  public void attemptPermanentFailure(Throwable error) {
    for (ApiTracer child : children) {
      child.attemptPermanentFailure(error);
    }
  }

  @Override
  public void lroStartFailed(Throwable error) {
    for (ApiTracer child : children) {
      child.lroStartFailed(error);
    }
  }

  @Override
  public void lroStartSucceeded() {
    for (ApiTracer child : children) {
      child.lroStartSucceeded();
    }
  }

  @Override
  public void responseReceived() {
    for (ApiTracer child : children) {
      child.responseReceived();
    }
  }

  @Override
  public void requestSent() {
    for (ApiTracer child : children) {
      child.requestSent();
    }
  }

  @Override
  public void batchRequestSent(long elementCount, long requestSize) {
    for (ApiTracer child : children) {
      child.batchRequestSent(elementCount, requestSize);
    }
  }

  /**
   * Get the attempt number of the current call. Attempt number for the current call is passed in
   * and recorded in {@link #attemptStarted(int)}. With the getter we can access it from {@link
   * ApiCallContext}. Attempt number starts from 0.
   */
  public int getAttempt() {
    return attempt;
  }
}