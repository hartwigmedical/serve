# CKB-Importer

[CKB](https://ckbhome.jax.org) - **C**linical **K**nowledge**b**ase - is a cancer knowledgebase provided by Jackson Lab. 
 
 This module imports the data that is provided in json format (CKB FLEX), and in addition does the following:
  *  For each molecular profile, determines the event type as defined by the [SERVE](../algo/README.md) datamodel.
  *  Provides classification mapping to map all molecular profiles onto [SERVE](../algo/README.md) actionable events.
  *  Loads up the database into a MySQL database
  
 ## Loading the CKB FLEX database into a MySQL database
 
 In order to load the CKB FLEX database into a MySQL database, one needs to:
  * Create the database using generate_ckb_db.sql script from the ckb-importer resources.
  * Run CkbImporterApplication (default class in the ckb-importer jar) with the following arguments:
  
| Argument | Description                                                               |
|----------|---------------------------------------------------------------------------|
| ckb_dir  | Required: Path to the directory holding the JSON data.                    |
| db_url   | Required: The URL of the database in which to ingest the data.            |
| db_user  | Required: The DB user which has access to the the URL specified           |
| db_pass  | Required: The password needed for the DB user to authenticate on the URL. |

## Version History and Download Links
- [Upcoming]
  - Support ingestion of drug class treatment approach
- [3.4.0](https://github.com/hartwigmedical/serve/releases/tag/ckb-importer-v3.4.0)
  - Support ingestion of new acronym field for clinical trials
- [2.2.0](https://github.com/hartwigmedical/serve/releases/tag/ckb-importer-v2.2.0)
  - Added "shortJournalTitle" and "pages" to reference json in ckb
- [2.1](https://github.com/hartwigmedical/serve/releases/tag/ckb-importer-v2.1)
  - Update size of column 'state' in table 'location'
- [2.0](https://github.com/hartwigmedical/serve/releases/tag/ckb-importer-v2.0)
  - Added fields to variant datamodel
    - hotspotReference (String)
    - isHotspot (boolean)
- [1.4](https://github.com/hartwigmedical/serve/releases/tag/ckb-importer-v1.4)
  - Added fields to variant datamodel: 
    - associatedWithDrugResistance ("Y" or null)
    - transformingActivity ("Y" or null) 
    - polymorphism ("Y" or null)
  - Move to new repo serve
- [1.3](https://github.com/hartwigmedical/hmftools/releases/tag/ckb-importer-v1.3)
  - Implementation of treatment approaches into the datamodel
  - Study phase could be nullable 
- [1.2](https://github.com/hartwigmedical/hmftools/releases/tag/ckb-importer-v1.2)
  - Support for java11
- [1.1](https://github.com/hartwigmedical/hmftools/releases/tag/ckb-importer-v1.1)
- [1.0](https://github.com/hartwigmedical/hmftools/releases/tag/ckb-importer-v1.0)
  - Initial release. 