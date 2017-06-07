#!/bin/bash

# Dump the shader file into the appropriate Java string.
function updateShader {
  orig=$1
  min=$2
  java=$3

  # compress shader to a single line
  cat $orig | sed 's/$/\\n/' | tr -d '\r\n' | tr -d '\n' > $min

  # find the line number in the java file to replace
  search='private final String fragmentShader'
  line=$(grep -n "$search" $java | cut -f1 -d:)

  # replace the shader line
  cat \
    <(head -n "$((line - 1))" $java) \
    <(echo "  ${search} = \"$(cat $min)\";") \
    <(tail -n "+$((line + 1))" $java) \
    > temp
  mv temp $java

  echo "Updated fragmentShader in $java"
}

# Update flex shader
updateShader \
  src/main/java/mod/flex.fs \
  src/main/java/mod/flex.min.fs \
  src/main/java/mod/render360/coretransform/render/Flex.java
