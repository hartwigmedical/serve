SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `knownHotspot`;
CREATE TABLE `knownHotspot`
(   `id` int NOT NULL AUTO_INCREMENT,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `proteinEffect` varchar(50) NOT NULL,
    `associatedWithDrugResistance` tinyint(1),
    `chromosome` varchar(50) NOT NULL,
    `position` varchar(50) NOT NULL,
    `ref` varchar(100) NOT NULL,
    `alt` varchar(100) NOT NULL,
    `inputTranscript` varchar(50),
    `inputProteinAnnotation` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownCodon`;
CREATE TABLE `knownCodon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `proteinEffect` varchar(50) NOT NULL,
    `associatedWithDrugResistance` tinyint(1),
    `chromosome` varchar(50) NOT NULL,
    `start` varchar(50) NOT NULL,
    `end` varchar(50) NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    `inputTranscript` varchar(50) NOT NULL,
    `inputCodonRank` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownExon`;
CREATE TABLE `knownExon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `proteinEffect` varchar(50) NOT NULL,
    `associatedWithDrugResistance` tinyint(1),
    `chromosome` varchar(50) NOT NULL,
    `start` varchar(50) NOT NULL,
    `end` varchar(50) NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    `inputTranscript` varchar(50) NOT NULL,
    `inputExonRank` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownGene`;
CREATE TABLE `knownGene`
(   `id` int NOT NULL AUTO_INCREMENT,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownCopyNumber`;
CREATE TABLE `knownCopyNumber`
(   `id` int NOT NULL AUTO_INCREMENT,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `proteinEffect` varchar(50) NOT NULL,
    `associatedWithDrugResistance` tinyint(1),
    `event` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownFusion`;
CREATE TABLE `knownFusion`
(   `id` int NOT NULL AUTO_INCREMENT,
    `geneUp` varchar(50) NOT NULL,
    `minExonUp` varchar(50),
    `maxExonUp` varchar(50),
    `geneDown` varchar(50) NOT NULL,
    `minExonDown` varchar(50),
    `maxExonDown` varchar(50),
    `proteinEffect` varchar(50) NOT NULL,
    `associatedWithDrugResistance` tinyint(1),
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `efficacyEvidence`;
CREATE TABLE `efficacyEvidence`
(   `id` int NOT NULL AUTO_INCREMENT,
    `source` varchar(50) NOT NULL,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `indication` varchar(700)  NOT NULL,
    `molecularCriteriumId` INT NOT NULL,
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `evidenceDirection` varchar(50) NOT NULL,
    `evidenceYear` int NOT NULL,
    `evidenceUrls` varchar(1000) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableTrial`;
CREATE TABLE `actionableTrial`
(   `id` int NOT NULL AUTO_INCREMENT,
    `source` varchar(50) NOT NULL,
    `nctId` varchar(20),
    `title` varchar(500),
    `acronym` varchar(100),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `therapyNames` varchar(2000),
    `genderCriterium` varchar(50),
    `indications` varchar(2000),
    `urls` varchar(100),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `molecularCriterium`;
CREATE TABLE `molecularCriterium`
(   `id` int NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `trialMolecularCriterium`;
CREATE TABLE `trialMolecularCriterium`
(   `actionableTrialId` int NOT NULL,
    `molecularCriteriumId` int NOT NULL
);

DROP TABLE IF EXISTS `actionableHotspot`;
CREATE TABLE `actionableHotspot`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `position` int NOT NULL,
    `ref` varchar(100) NOT NULL,
    `alt` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableCodon`;
CREATE TABLE `actionableCodon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `start` int NOT NULL,
    `end` int NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableExon`;
CREATE TABLE `actionableExon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `start` int NOT NULL,
    `end` int NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableGene`;
CREATE TABLE `actionableGene`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `gene` varchar(50) NOT NULL,
    `event` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableFusion`;
CREATE TABLE `actionableFusion`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `geneUp` varchar(50) NOT NULL,
    `minExonUp` int,
    `maxExonUp` int,
    `geneDown` varchar(50) NOT NULL,
    `minExonDown` int,
    `maxExonDown` int,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableCharacteristic`;
CREATE TABLE `actionableCharacteristic`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `type` varchar(50) NOT NULL,
    `cutoffType` varchar(50),
    `cutoff` double precision,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableHla`;
CREATE TABLE `actionableHla`
(   `id` int NOT NULL AUTO_INCREMENT,
    `molecularCriteriumId` int NOT NULL,
    `sourceDate` DATE NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `hlaAllele` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `eventInterpretation`;
CREATE TABLE `eventInterpretation`
(   `id` int NOT NULL AUTO_INCREMENT,
    `source` varchar(100) NOT NULL,
    `sourceEvent` varchar(250),
    `interpretedGene` varchar(100),
    `interpretedEvent` varchar(250) NOT NULL,
    `interpretedEventType` varchar(100),
    PRIMARY KEY (`id`)
);

SET FOREIGN_KEY_CHECKS = 1;