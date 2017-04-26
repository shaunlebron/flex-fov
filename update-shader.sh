#!/bin/bash

# Dump the shader file into the appropriate Java string.

orig=src/main/java/mod/flex.fs
min=src/main/java/mod/flex.min.fs
java=src/main/java/mod/render360/coretransform/render/Flex.java
temp=temp

search='private final String fragmentShader'

cat $orig | sed 's/$/\\n/' | tr -d '\r\n' | tr -d '\n' > $min

line=$(grep -n "$search" $java | cut -f1 -d:)

cat \
  <(head -n "$((line - 1))" $java) \
  <(echo "  ${search} = \"$(cat $min)\";") \
  <(tail -n "+$((line + 1))" $java) \
  > $temp

mv $temp $java

echo "Updated fragmentShader in $java"
