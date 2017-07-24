#!/usr/bin/env bash

for arg in "$*"
do
   java -cp libs/mystorm-core.jar com.wojustme.mystorm.submitter.SubmiterHelper $arg
done