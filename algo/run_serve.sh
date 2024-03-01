#!/usr/bin/env bash

set -eo pipefail

# fail if not exactly one argument is given
if [ "$#" -ne 1 ]; then
    echo "Unzips the ckb input, then runs serve on it."
    echo "Usage: $0 <version>"
    exit 1
fi

version=$1
ckb_path="/data/resources/custom/ckb/production"

mkdir -p $ckb_path
echo "Unzipping ckb input... from /in/ckb/api-export-$version.zip to $ckb_path/api-export"
unzip -o "/in/ckb/api-export-$version.zip" -d $ckb_path > /dev/null
echo "Unzipped ckb input."
echo "Running serve... with ckb path $ckb_path/api-export"
java -jar /opt/app/serve.jar -properties_file /opt/app/serve.properties -ckb_dir "$ckb_path/api-export"