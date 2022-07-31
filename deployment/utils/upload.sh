#!/bin/bash

# required: jq (for json query)

USERNAME="share"
PASSWORD="noadmin"
JWT="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbnRnLm5ldC9pc3N1ZXIiLCJ1cG4iOiJwdzogbm9hZG1pbiIsImdyb3VwcyI6WyJVc2VyIl0sImF1dGhfdGltZSI6MTY1OTI4MzQ3NjM2Miwic3ViIjoiMzAwMiIsImlhdCI6MTY1OTI4MzQ3NiwiZXhwIjoxNjU5MzY5ODc2LCJqdGkiOiIzYzFhMjQ2NS1lYjgwLTQxZTItOTU2ZS1iMDdjMDM0MGYyNGMifQ.AZEWMHnmUCHn0Cpp3MqOjhMjilmK5DG0svGyb7hMPARlOuZm4NRTQ56aIJj5s6XwWKBteD4Y21U5D5-5o3D-S9SGabWbMgcPYdbHWJcLC1tmBnLaiatSMLxRBWd-hJgSI6x2Dyt-UnyCKWOG-bqBjTGoGQ-EO3nD20VtiDC4iB0SEHpPwbmWTPSWJKCtqXC_WQZDvw3BctrOadVSrHz9-kk-qriagFdXojCil8Xey3Nw1tXAEXNBGTbjpK4C1rVonGWz8BB1z2LAWA6XOYRr3oJDaLs9XFPF_FWY9cEx9gR7H_kBpdpfzs77SQr6ps2IHp2wxBGgESDX6Eg2FZZ-iA"
count=0;

login() { # <username> <password> -> <token_string>
    RET=$(curl -X 'POST' \
        'http://dashboard.ulake.usth.edu.vn/api/user/login' \
        -H 'accept: */*' \
        -H 'Content-Type: application/json' \
        -d "{
          \"password\": \"$2\",
          \"userName\": \"$1\"
    }")
    echo $(jq -r ".resp" <<<$RET)
}

mkFolder() { # <parent_id> <folder_name> -> <subfolder_id>
    RET=$(curl -X 'POST' \
        'http://dashboard.ulake.usth.edu.vn/api/folder' \
        -H 'accept: */*' \
        -H "Authorization: Bearer $JWT" \
        -H 'Content-Type: application/json' \
        -d "{
          \"name\": \"$2\",
          \"parent\": {\"id\": $1 }
      }")
    echo $(jq -r ".resp.id" <<< $RET)
}

pushFile() { # <mime> <name> <size> <parent_id>
    # for debug, uncomment the line below
    # set -x
    curl -X "POST" \
        --progress-bar \
        http://dashboard.ulake.usth.edu.vn/api/file \
        -H "accept: */*" \
        -H "Authorization: Bearer $JWT" \
        -H "Content-Type: multipart/form-data" \
        -F "fileInfo={
                \"mime\": \"$1\",
                \"name\": \"$2\",
                \"size\": $3,
                \"parent\": { \"id\": $4 }
          };type=application/json" \
        -F "file=@$2;type=application/octet-stream" > /dev/null
    set +x
    count=$(( $count + 1 ))
}

pushDir() { # <parent_folder_id> <prefix-depth>
    local currDir=$(pwd)
    local parentId=$1
    local depth=$2
    local prefix="$(printf '%*s' $depth | tr ' ' '_')"

    for name in *; do
        # echo "$prefix Item: $name, parent $parentId"
        if [[ -d "$name" ]]; then
            sub=$(mkFolder $parentId "$name");
            echo "$prefix [$count/$total] $parentId <- FOLDER $name"
            cd "$name"
            nextDepth=$(( $depth + 1 ))
            pushDir "$sub" "$nextDepth"
            cd "$currDir"
        else
            echo "$prefix [$count/$total] $parentId <- file $name"
            mime=$(file -b --mime-type "$name")
            size=$(wc -c "$name" | awk '{print $1}')
            pushFile "$mime" "$name" "$size" "$parentId"
        fi
    done
}

usage() {
    echo "Usage: ulake_upload_script.sh <lake_folder_id>

      Script discoveries recursively all subfolders in CURRENT directory and
      synchronize them to corresponding structure on lake storage.
      "
}

if [[ $# == 0 ]]; then
    usage
else
    folder_id=$1
    shift 1
    #JWT=$(login $USERNAME $PASSWORD)
    echo "Counting files..."
    total=$( find . -type f | wc -l )
    echo "Starting pushing $total files..."
    pushDir $folder_id 1
fi
