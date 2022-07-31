#!/bin/bash

USERNAME="share"
PASSWORD="noadmin"

login() { # <username> <password> -> <token_string>
    RET=$(curl -X 'POST' \
          'http://dashboard.ulake.usth.edu.vn/api/user/login' \
          -H 'accept: */*' \
          -H 'Content-Type: application/json' \
          -d "{
          \"password\": \"$2\",
          \"userName\": \"$1\"
    }")
    echo $(jq -r ".resp" <<< $RET);
}

create_folder() { # <token> <parent_id> <folder_name> -> <subfolder_id>
      RET=$(curl -X 'POST' \
          'http://dashboard.ulake.usth.edu.vn/api/folder' \
          -H 'accept: */*' \
          -H "Authorization: Bearer $1" \
          -H 'Content-Type: application/json' \
          -d "{
          \"name\": \"$3\",
          \"parent\": {\"id\": $2}
      }")
      echo $(jq -r ".resp.id" <<< $RET);
}

create_file() { # <token> <mime> <name> <size> <parent_id> <path_to_file>
      echo $6;
      RET=$(curl -X 'POST' \
          'http://dashboard.ulake.usth.edu.vn/api/file' \
          -H 'accept: */*' \
          -H "Authorization: Bearer $1" \
          -H 'Content-Type: multipart/form-data' \
          -F "fileInfo={
                \"mime\": \"$2\",
                \"name\": \"$3\",
                \"parent\": { \"id\": $5 },
                \"size\": $4
          };type=application/json" \
      -F "file=@$6;type=application/octet-stream")
      echo $RET; # debug only
}

recusion_path() { # <parent_folder_id> <token> <path>
      local PARENT_ID=$1;
      local TOKEN=$2;
      shift 2;

      for i in $@; do
            echo -e "Process at folder id: $PARENT_ID";
            name=$(basename $i);
            if [[ -d $i ]]; then
                  sub=$(create_folder $TOKEN $PARENT_ID $name);
                  echo "Lake folder add ($i) to ($PARENT_ID) with name ($name)...";
                  recusion_path $sub $TOKEN $(ls -d $i/*);
            else
                  echo "Lake file add ($i) with name ($name)...";
                  mime=$(file -b --mime-type $i);
                  size=$(wc -c $i | awk '{print $1}');
                  create_file $TOKEN $mime $name $size $PARENT_ID $i
            fi;
      done;
}

usage() {
      echo "Usage: ulake_upload_script.sh <lake_folder_id> <folder_list_to_lake>

      Script discoveries recursively all subfolder of folder in folder_list and
      synchronize them to corresponding structure on lake storage.
      ";
}

if [[ $# == 0 ]]; then
      usage;
else
      folder_id=$1; shift 1;
      recusion_path $folder_id $(login $USERNAME $PASSWORD) $@;
fi;
