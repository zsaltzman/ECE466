#!/bin/bash

java TrafficSink 4445 &
java TrafficGenerator 4445 localhost