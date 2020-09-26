#!/bin/bash

PASS=changeme

OP=$1
if [ -z "$1" ]; then
    OP=all
fi

echo ""
echo "Database operation create: $OP"
echo ""

case "$OP" in

    all)

        echo "Creating database..."
        mysql -u root -p$PASS < `dirname $0`/create-database.sql
        mysql -u root -p$PASS < `dirname $0`/create-users.sql
        mysql -u root -p$PASS < `dirname $0`/create-worldmap.sql

        ;&

    tiles)

        echo "Creating tiles..."
        mysql -u root -p$PASS < `dirname $0`/create-tiles.sql

        ;&

    resources)

        echo "Creating resources..."
        mysql -u root -p$PASS < `dirname $0`/create-resources.sql

        ;&

    structures)

        echo "Creating structures..."
        mysql -u root -p$PASS < `dirname $0`/create-structures.sql

        ;&

    objects)

        echo "Creating objects..."
        mysql -u root -p$PASS < `dirname $0`/create-objects.sql

        ;&


    *) 

        echo "Finished $OP"
        ;;

esac

