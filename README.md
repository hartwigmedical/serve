# SERVE

SERVE (Search External Resources for Variant Evidence) harmonizes various sources of evidence into a single unified model that can be
readily used in genomic analyses:

- A model is generated which allows mapping of genomic events to clinical evidence.
- An overview of mutations that are implied to be potential cancer drivers is generated.

In addition, this repo provides a number of utility applications to ingest and analyze various SERVE input sources.

| Module                                 | Description                               |
|----------------------------------------|-------------------------------------------|
| [Datamodel](datamodel)                 | The datamodel that other tools can reuse. |
| [Algo](algo)                           | The actual SERVE algorithm.               |
| [CKB-Importer](ckb-importer)           | Importer of CKB FLEX datamodel.           |
| [iClusion-Importer](iclusion-importer) | Importer of iClusion datamodel.           |
| [VICC-Importer](algo)                  | Importer of VICC datamodel.               |

## Releasing serve-datamodel

The `serve-datamodel` module is used by external projects and is deployed to GCP's artifact repository.
To release a new version of the datamodel artifact, perform the following:

```shell
gcloud auth application-default login
mvn versions:set -DnewVersion={new_version}
mvn -pl \!ckb-importer,\!iclusion-importer,\!vicc-importer deploy
git tag serve-v{new_version}
git push origin serve-v{new_version}
```

When complete you can roll back the changes to the pom.xmls. These are meant to be transient.

Note the new version should be of the format `major.minor.patch` where:

- Major indicates a non-backward compatible change (avoid these!)
- Minor indicates a new feature
- Patch indicates a bug fix

## Upgrading serve-algo

The `serve-algo` module contains the algo producing the `serve-datamodel`. The following steps are required to upgrade:

1. Set the version in the pom.xml file in serve-algo (typically identical to the version of serve-datamodel)
2. Compile and package locally using `mvn clean package`
3. Update the `deploy_serve_prod` script and run this script to copy the algo jar with the correct name in the common-tools bucket.
4. Roll back the change in the pom.xml file in serve-algo
4. Create a release using the GitHub website and attach the same jar (and serve sql) as additional resources.
5. Add release notes to README.md in serve-algo

The VMs used to run SERVE automatically sync the common-tools bucket so will eventually have the new algo jar.

Note that if you upgrade `serve-algo` without creating an associated `serve-datamodel`, you need to tag the commit used for the `serve-algo`
release!

