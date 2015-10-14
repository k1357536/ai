#!/bin/bash
# this script runs all the tests, provided the name
# of the tests' sourcefile starts with 'Test' ...

# find all the tests in the source tree
tests=$(find src/at/jku/cp/ai/tests -name "Test*.java")

# find the junit4.jar
junit=$(locate java/junit4.jar)

# find all the libraries, concatenate them into "lib0.jar:lib1.jar:lib2.jar:"
libs=""
for l in $(ls libs/*.jar);
do
    libs="${l}:${libs}"
done

# the call for the java compiler
java_compiler="javac -target 1.8 -source 1.8 -cp .:src:${libs}${junit}"

# the call for the java runtime
java_runtime="java -cp .:./src:${libs}${junit} org.junit.runner.JUnitCore"

# for all test sourcefiles:
for test in $tests
do
    # tell what we are doing
    echo "${test}"
    
    # convert the source-filename into a class-filename
    testclassname=$(echo "${test}" | sed -e "s/src\///" -e "s/\.java//" -e "s/\//./g")

    # compile test
    $java_compiler $test
    
    # run test
    $java_runtime $testclassname
done
