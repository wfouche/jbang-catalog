#!/usr/bin/env python-jvm

# restclient_jython.py
#
# Run: jbang run python-jvm@wfouche restclient_jython.py
#

# /// jbang
# requires-jython = "==2.7.4"
# requires-java = ">=21"
# debug = false
# dependencies = [
#   "org.springframework.boot:spring-boot-starter-web:3.4.4",
# ]
# [java]
#   runtime-options = "-server -Xms2g -Xmx2g -XX:+UseZGC -XX:+ZGenerational"
# ///

import java.lang.String as String
import org.springframework.web.client.RestClient as RestClient

def restApiCall(uri, id):
    restClient = RestClient.create()
    rsp = restClient.get().uri(uri, id).retrieve().body(String)
    print(rsp)

def main():
    restApiCall("https://jsonplaceholder.typicode.com/todos/{id}", 1)

main()
