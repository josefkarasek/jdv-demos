```
# Create new project
oc new-project jdv-datafederation

# Create Image Stream if doesn't exist (performed by OpenShift administrator)
oc create -f https://raw.githubusercontent.com/josefkarasek/jdv-demos/master/datafederation/datavirt-is.yaml -n openshift

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.yaml
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):datavirt-service-account -n $(oc project -q)

# Create Template
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-extensions-support-s2i.json

# Process template
oc new-app --template=datavirt63-extensions-support-s2i -p TEIID_USERNAME=teiidUser -p TEIID_PASSWORD=JBoss.123

# Done. You can access the application:
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dorg.teiid.ssl.protocol=TLSv1.2 -Dexec.args='<jdbc-jdv-app-route>'
```
