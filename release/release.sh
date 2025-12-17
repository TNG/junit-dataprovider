#!/bin/bash

set -e

function parse_all_gradle_properties() {
    # make sure output_target is an array variable
    [[ "$(declare -p $1 2>/dev/null)" =~ "declare -a" ]] || return 11
    local -n output_target=$1
    for command_line_argument in "$@"; do
        # restrict to Gradle property syntax
        if [[ "${command_line_argument}" =~ ^-P.*=.* ]];then
          output_target+=("${command_line_argument}")
        fi
    done
}

echo "Beginning"
declare -a GRADLE_PROPERTIES
parse_all_gradle_properties GRADLE_PROPERTIES "$@"

echo Building and testing...
./gradlew --scan build

echo Publishing...
./gradlew --no-parallel -Prelease publishAllPublicationsToMavenCentralRepository "${GRADLE_PROPERTIES[@]}"
