///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.github.wfouche.tulip:tulip-runtime:2.2.0
//SOURCES JavaHttpUser.java
//JAVA 21
//FILES ../../benchmark_config.json
//FILES ../../logback.xml
//RUNTIME_OPTIONS -XX:+IgnoreUnrecognizedVMOptions
//RUNTIME_OPTIONS --enable-native-access=ALL-UNNAMED
//RUNTIME_OPTIONS --sun-misc-unsafe-memory-access=allow

package io.tulip;

import io.github.wfouche.tulip.api.TulipApi;

public class App {
   public static void main(String[] args) {
      TulipApi.runTulip("benchmark_config.json");
   }
}
