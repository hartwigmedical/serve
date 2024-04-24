
ALTER TABLE actionableHotspot
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

ALTER TABLE actionableCodon
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

ALTER TABLE actionableExon
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

ALTER TABLE actionableGene
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

ALTER TABLE actionableFusion
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

 ALTER TABLE actionableCharacteristic
     ADD COLUMN studyNctId varchar(100) after sourceUrls,
     ADD COLUMN studyTitle varchar(500) after studyNctId,
     ADD COLUMN countriesOfStudy varchar(500) after studyTitle;

ALTER TABLE actionableHla
    ADD COLUMN studyNctId varchar(100) after sourceUrls,
    ADD COLUMN studyTitle varchar(500) after studyNctId,
    ADD COLUMN countriesOfStudy varchar(500) after studyTitle;
