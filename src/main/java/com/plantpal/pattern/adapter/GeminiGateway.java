package com.plantpal.pattern.adapter;
/** Isolates HTTP transport from response adaptation, making failure tests deterministic. */
@FunctionalInterface
public interface GeminiGateway {
 String generate(String requestJson);
}
