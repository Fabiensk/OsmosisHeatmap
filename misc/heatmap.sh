#!/bin/bash

# For a given Openstreetmap PBF dump, create a JavaScript file with the coordinates
#  of the nodes and ways matching a given tag.
#
# Parameter:
# - either tag name (ex: shop)
# - or tag name name and value (ex: shop=bakery) 
#
# Note

# default values for input and output and bounding box.
infile=/mnt/p2p/osm/ile-de-france-latest.osm.pbf
output=$PWD/heatmap_data.js
bounding_box="left=2.2563171 right=2.4176788 top=48.903997399999994 bottom=48.8159071"

# searched tag (default values)
searched_tag=addr:housenumber
searched_val=""

# Parse the script arguments
if [ $# -eq 1 ]
then
    tmp_array=(${1//=/ })
    searched_tag="${tmp_array[0]}"
    if [ "${tmp_array[1]}" != "" ]
    then
        searched_val="${tmp_array[1]}"
    fi
fi  

if [ "$searched_val" == "" ]
then
    # match key
    node_cmd="--node-key"    
    node_cmd_val="keyList=$searched_tag"
    way_cmd_val="$searched_tag=*"
else 
    # match key and value
    node_cmd="--node-key-value"    
    node_cmd_val="keyValueList=$searched_tag.$searched_val"
    way_cmd_val="$searched_tag=$searched_val"
fi

echo "$node_cmd" "$node_cmd_val" 
echo "$way_cmd_val"

cd /home/fab/programs/osm/osmosis-latest

./bin/osmosis \
--read-pbf file="$infile" outPipe.0=1 \
--bounding-box $bounding_box inPipe.0=1 outPipe.0=2 \
--tee 2 inPipe.0=2 outPipe.0=3 outPipe.1=4 \
"$node_cmd" "$node_cmd_val" inPipe.0=3 outPipe.0=8 \
--heatmapmerge node output="$output" inPipe.0=8 \
--tag-filter accept-ways "$way_cmd_val" inPipe.0=4 outPipe.0=6 \
--tag-filter reject-relations inPipe.0=6 outPipe.0=7 \
--used-node inPipe.0=7 outPipe.0=9 \
--tag-filter reject-relations inPipe.0=9 outPipe.0=10 \
--heatmapmerge way output="$output" inPipe.0=10


