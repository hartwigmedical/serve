# SERVE

SERVE (Search External Resources for Variant Evidence) harmonizes various sources of evidence into a single unified model that can be readily used in genomic analyses:
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
```

When complete you can roll back the changes to the pom.xmls. These are meant to be
transient. 

Note the new version should be of the format `major.minor.patch` where:
- Major indicates a non-backward compatible change (avoid these!)
- Minor indicates a new feature
- Patch indicates a bug fix
