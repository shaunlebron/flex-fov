#!/bin/bash


cat src/main/java/mod/flex.fs | tr '\r\n' ' ' > src/main/java/mod/flex.min.fs
