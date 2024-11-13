SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `actionableHotspot`;
CREATE TABLE `actionableHotspot`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `position` int NOT NULL,
    `ref` varchar(100) NOT NULL,
    `alt` varchar(100) NOT NULL,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` varchar(5000),
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableCodon`;
CREATE TABLE `actionableCodon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `start` int NOT NULL,
    `end` int NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` varchar(5000),
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableExon`;
CREATE TABLE `actionableExon`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `gene` varchar(50) NOT NULL,
    `chromosome` varchar(50) NOT NULL,
    `start` int NOT NULL,
    `end` int NOT NULL,
    `applicableMutationType` varchar(50) NOT NULL,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableGene`;
CREATE TABLE `actionableGene`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `gene` varchar(50) NOT NULL,
    `event` varchar(50) NOT NULL,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableFusion`;
CREATE TABLE `actionableFusion`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `geneUp` varchar(50) NOT NULL,
    `minExonUp` int,
    `maxExonUp` int,
    `geneDown` varchar(50) NOT NULL,
    `minExonDown` int,
    `maxExonDown` int,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableCharacteristic`;
CREATE TABLE `actionableCharacteristic`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `type` varchar(50) NOT NULL,
    `cutoffType` varchar(50),
    `cutoff` double precision,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `actionableHla`;
CREATE TABLE `actionableHla`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `hlaAllele` varchar(50) NOT NULL,
    `source` varchar(50) NOT NULL,
    `entryDate` DATETIME NOT NULL,
    `sourceEvent` varchar(50) NOT NULL,
    `sourceUrls` varchar(2000),
    `nctId` varchar(100),
    `title` varchar(500),
    `acronym` varchar(250),
    `genderCriterium` varchar(50),
    `countriesAndCities` varchar(1000),
    `hospitalsPerCity` text,
    `treatment` varchar(1000) NOT NULL,
    `treatmentApproachesDrugClass` varchar(500),
    `treatmentApproachesTherapy` varchar(500),
    `applicableCancerType` varchar(100) NOT NULL,
    `applicableDoid` varchar(50) NOT NULL,
    `blacklistCancerTypes` varchar(500),
    `efficacyDescription` varchar(1000) NOT NULL,
    `evidenceYear` int,
    `evidenceLevel` varchar(50) NOT NULL,
    `evidenceLevelDetails` varchar(100) NOT NULL,
    `direction` varchar(50) NOT NULL,
    `evidenceUrls` varchar(1000),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownHotspot`;
CREATE TABLE `knownHotspot`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
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
    `modified` DATETIME NOT NULL,
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
    `modified` DATETIME NOT NULL,
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
    `modified` DATETIME NOT NULL,
    `gene` varchar(50) NOT NULL,
    `geneRole` varchar(50) NOT NULL,
    `sources` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `knownCopyNumber`;
CREATE TABLE `knownCopyNumber`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
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
    `modified` DATETIME NOT NULL,
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

DROP TABLE IF EXISTS `eventInterpretation`;
CREATE TABLE `eventInterpretation`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `source` varchar(100) NOT NULL,
    `sourceEvent` varchar(250),
    `interpretedGene` varchar(100),
    `interpretedEvent` varchar(250) NOT NULL,
    `interpretedEventType` varchar(100),
    PRIMARY KEY (`id`)
);

SET FOREIGN_KEY_CHECKS = 1;