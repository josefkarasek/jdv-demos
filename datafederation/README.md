```
# Create new project
oc new-project jdv-datafederation

# Create Image Stream if doesn't exist (performed by OpenShift administrator)
oc create -f https://raw.githubusercontent.com/josefkarasek/jdv-demos/master/datafederation/datavirt-is.yaml -n openshift

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.yaml

# Create Template
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-extensions-support-s2i.json

# Process template
oc process datavirt63-extensions-support-s2i -v TEIID_USERNAME=teiidUser -v TEIID_PASSWORD=JBoss.123 | oc create -f -

# Done. You can access the application:
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dexec.args='<jdbc-jdv-app-route>'
```
