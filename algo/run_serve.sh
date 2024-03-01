#!/usr/bin/env bash

set -eo pipefail

# fail if less than 2 args are provided
if [ "$#" -ne 2 ]; then
    echo "Unzips the ckb input, then runs serve on it."
    echo "Usage: $0 <version>"
    exit 1
fi

version=$1
ckb_path="/data/resources/custom/ckb/production"

mkdir -p $ckb_path
unzip -o "/in/ckb/api-export-$version.zip" -d $ckb_path > /dev/null
java -jar /opt/app/serve.jar -properties_file /opt/app/serve.properties -ckb_dir "$ckb_path/api-export"