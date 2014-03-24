package org.robotninjas.concurrent;

import com.google.common.base.Function;
import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class FluentWrapper<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements FluentFuture<V> {

  private final Executor executor;

  FluentWrapper(ListenableFuture<V> future, Executor executor) {
    super(future);
    this.executor = executor;
  }

  FluentWrapper(ListenableFuture<V> future) {
    this(future, MoreExecutors.sameThreadExecutor());
  }

  @Override
  public <Y> FluentFuture<Y> transform(Function<V, Y> func) {
    return new FluentWrapper<>(Futures.transform(this, func));
  }

  @Override
  public <Y> FluentFuture<Y> transform(Function<V, Y> func, Executor executor) {
    return new FluentWrapper<>(Futures.transform(this, func, executor), this.executor);
  }

  @Override
  public <Y> FluentFuture<Y> transform(AsyncFunction<V, Y> func) {
    return new FluentWrapper<>(Futures.transform(this, func));
  }

  @Override
  public <Y> FluentFuture<Y> transform(AsyncFunction<V, Y> func, Executor executor) {
    return new FluentWrapper<>(Futures.transform(this, func, executor), this.executor);
  }

  @Override
  public FluentFuture<V> withFallback(FutureFallback<V> fallback) {
    return new FluentWrapper<>(Futures.withFallback(this, fallback));
  }

  @Override
  public FluentFuture<V> withFallback(FutureFallback<V> fallback, Executor executor) {
    return new FluentWrapper<>(Futures.withFallback(this, fallback, executor), this.executor);
  }

  @Override
  public FluentFuture<V> addCallback(FutureCallback<V> callback) {
    Futures.addCallback(this, callback);
    return this;
  }

  @Override
  public FluentFuture<V> addCallback(FutureCallback<V> callback, Executor executor) {
    Futures.addCallback(this, callback, executor);
    return this;
  }

  @Override
  public FluentFuture<V> onSuccess(final Consumer<V> callback, Executor executor) {
    return addCallback(ConsumerWrapper.success(callback), executor);
  }

  @Override
  public FluentFuture<V> onSuccess(final Consumer<V> callback) {
    return onSuccess(callback, MoreExecutors.sameThreadExecutor());
  }

  @Override
  public FluentFuture<V> onFailure(final Consumer<Throwable> callback, Executor executor) {
    return addCallback(ConsumerWrapper.<V>failure(callback), executor);
  }

  @Override
  public FluentFuture<V> onFailure(final Consumer<Throwable> callback) {
    return onFailure(callback, MoreExecutors.sameThreadExecutor());
  }

  @Override
  public <E extends Exception> FluentCheckedFuture<V, E> makeChecked(Function<Exception, E> func) {
    return new CheckedWrapper<>(Futures.makeChecked(this, func));
  }

  @Override
  public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.get(l, timeUnit);
  }

  @Override
  public <X extends Exception> V get(long l, TimeUnit timeUnit, Class<X> exceptionClass) throws X {
    return Futures.get(this, l, timeUnit, exceptionClass);
  }

  @Override
  public <E extends Exception> V get(Class<E> exceptionClass) throws E {
    return Futures.get(this, exceptionClass);
  }

}
