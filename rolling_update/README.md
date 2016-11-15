Demonstration of Rolling update with JDV in OpenShift.
Note that clients can loose connection during update. 
```
oc process jdv63-secure-s2i \
-v TEIID_USERNAME='teiidUser' \
-v TEIID_PASSWORD='JBoss.123' \
-v SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
-v SOURCE_REPOSITORY_REF=master \
-v CONTEXT_DIR='rolling_update/v1' | oc create -f -
```