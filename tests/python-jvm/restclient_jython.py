#!/usr/bin/env ./python-run.py
# restclient_jython.py
#
# Run as: $ jbang run python-jvm@wfouche restclient_jython.py
#

# /// jbang
# Requires-jython = "==2.7.4"
# requires-java = ">=21"
# dependencies = [
#   "org.springframework.boot:spring-boot-starter-web:3.4.4",
# ]
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
