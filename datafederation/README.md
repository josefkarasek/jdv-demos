```
# Create Image Stream
cat <<EOF | oc create -n openshift -f -
{
  "kind": "ImageStream",
  "apiVersion": "v1",
  "metadata": {
    "name": "jboss-datavirt63-openshift"
  },
  "spec": {
    "dockerImageRepository": "<docker_repository_url>"
  }
}
EOF

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/jdv-app-secret.json

# Create Template
oc create  -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/jdv63-extensions-support-s2i.json

# Create and link Secrets. This requires files from two git repositories.
# 1) datasources & resource adapters
git clone https://github.com/jboss-openshift/openshift-quickstarts
cd openshift-quickstarts/datavirt/dynamicvdb-datafederation/derby
oc secrets new jdv-app-config datasources.env resourceadapters.env

# 2) excel and txt files
git clone https://github.com/teiid/teiid-quickstarts.git
cd teiid-quickstarts
git checkout 8.12
cd dynamicvdb-datafederation/src
oc secrets new jdv-app-excel-files teiidfiles/excelFiles/
oc secrets new jdv-app-data teiidfiles/data/
oc secrets link jdv-service-account jdv-app-data jdv-app-excel-files
# done

# Process template
oc process jdv63-extensions-support-s2i -v TEIID_USERNAME=teiidUser -v TEIID_PASSWORD=JBoss.123 | oc create -f -

# Setup mount point for the secrets
oc volume dc/jdv-app --add --name=data --mount-path=/teiidfiles/data --type=secret --secret-name=jdv-app-data
oc volume dc/jdv-app --add --name=excel-files --mount-path=/teiidfiles/excel-files --type=secret --secret-name=jdv-app-excel-files

# Done. You can access the application:
cd client
mvn package exec:java -Dexec.args='<jdbc-jdv-app-route>' -Dorg.teiid.ssl.trustStore=truststore.ts
```