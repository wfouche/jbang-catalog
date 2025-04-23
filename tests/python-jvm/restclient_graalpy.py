#!/usr/bin/env python-jvm

# restclient_graalpy.py
#
# Run: jbang run python-jvm@wfouche restclient_graalpy.py
#

# /// jbang
# requires-graalpy = "==24.2.1"
# requires-java = ">=21"
# dependencies = [
#   "org.springframework.boot:spring-boot-starter-web:3.4.4"
# ]
# [graalpy]
#   allowAllAccess = true
#   emulateJython = true
# [java]
#   runtime-options = "-XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -Dpolyglot.engine.WarnInterpreterOnly=false"
# [jbang]
#   integrations = false
# [python-jvm]
#   debug = false
# ///

import java.lang.String as String
import org.springframework.web.client.RestClient as RestClient

def restApiCall(uri: str, id: int):
    restClient = RestClient.create()
    rsp = restClient.get().uri(uri, id).retrieve().body(String)
    print(rsp)

def main():
    restApiCall("https://jsonplaceholder.typicode.com/todos/{id}", 1)

main()
