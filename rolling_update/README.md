Demonstration of Rolling update with JDV in OpenShift.
Note that clients can loose connection during update.
```
# Create new project
oc new-project jdv-rolling

# Create Image Stream if doesn't exist (performed by OpenShift administrator)
oc create -f https://raw.githubusercontent.com/josefkarasek/jdv-demos/master/datafederation/datavirt-is.yaml -n openshift

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.yaml
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):datavirt-service-account -n $(oc project -q)

# Create Template
oc create -f datavirt63-secure-rolling-s2i.json

# Create empty secret. This scenario uses no data source, so secret can be empty.
oc secrets new datavirt-app-config empty.env

oc process datavirt63-secure-rolling-s2i \
  -p TEIID_USERNAME='teiidUser' \
  -p TEIID_PASSWORD='JBoss.123' \
  -p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
  -p SOURCE_REPOSITORY_REF=master \
  -p CONTEXT_DIR='rolling_update/v1' | oc create -f -

# Wait for start of 5 replicas
# Test the application

# To start the rolling update edit Build Config to use VDB v2.
# Note that Config Change trigger on Build Config starts a new build only on creation of the BC, but not on change.
oc edit bc datavirt-app
oc start-build datavirt-app --follow

# Once the build has finished you'll be able to see progress of the rolling update either in the web UI or in terminal.
# Note that connections may fail during rolling update
```
