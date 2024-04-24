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

## Releasing serve

To release a new version of the `serve`, perform the following:

```shell
git tag {new_version}
git push origin {new_version}
```

This will automatically trigger a cloud build instance which will deploy the artifacts to both artifact registry and container registry.

Note the new version should be of the format `major.minor.patch` where:

- Major indicates a non-backward compatible change (avoid these!)
- Minor indicates a new feature
- Patch indicates a bug fix

Currently, the GitHub release is not automatically created, so you need to create a new release on the GitHub website and attach the serve jar as an additional resource.
The jars can be built using `mvn clean package`.
