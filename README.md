# Search External Resources for Variant Evidence

SERVE harmonizes various sources of evidence into a single unified model that can be readily used to interpret genomic analyses:

- A model is generated which allows mapping of genomic events to clinical evidence.
- An overview of mutations that are implied to be potential cancer drivers is generated.

In addition, this repo provides a number of utility applications to ingest and analyze various SERVE input sources.

| Module                         | Description                               |
|--------------------------------|-------------------------------------------|
| [Datamodel](datamodel)         | The datamodel that other tools can reuse. |
| [Algo](algo)                   | The actual SERVE algorithm.               |
| [CKB-Importer](ckb-importer)   | Importer of CKB FLEX datamodel.           |
| [VICC-Importer](vicc-importer) | Importer of VICC datamodel.               |

## Releasing serve

To release a new version of the `serve` and all submodules, perform the following:

```shell
git tag ${new_version}
git push origin ${new_version}
```

This will automatically trigger a cloud build instance which will deploy the artifacts to both artifact registry and container registry.

Note the new version should be of the format `major.minor.patch` where:

- Major indicates a non-backward compatible change (avoid these if possible!)
- Minor indicates a new feature
- Patch indicates a bug fix

Currently, the GitHub release is not automatically created, so you need to create a new release on the GitHub website and attach the
respective jar and database generation scripts as additional resources. The jars can be built by running these commands:

- `mvn versions:set -DnewVersion=${new_version}`
- `mvn clean package`
- `mvn versions:revert`

In addition, when creating releases on the GitHub website it is convenient to create additional tags with the module prefix (
e.g. `serve-v${new_version}` when releasing algo)
