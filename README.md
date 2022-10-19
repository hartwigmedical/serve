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
