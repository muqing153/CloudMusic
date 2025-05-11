package com.muqingbfq.mq;

@FunctionalInterface
public interface TaskAction<T> {
    void execute(T t);
}

