#!/bin/bash
#
# Source this script (run ". setenv.sh") to set up environment for dev
# 
export CLASSPATH=`ls lib/* | tr '\n' ':'`:generated-dist/frostbite-tiger.jar

