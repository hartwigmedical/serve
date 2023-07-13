DROP TABLE IF EXISTS actionableRange;

DROP TABLE IF EXISTS actionableCodon;
CREATE TABLE actionableCodon
(   id int NOT NULL AUTO_INCREMENT,
    modified DATETIME NOT NULL,
    gene varchar(50) NOT NULL,
    chromosome varchar(50) NOT NULL,
    start int NOT NULL,
    end int NOT NULL,
    applicableMutationType varchar(50) NOT NULL,
    source varchar(50) NOT NULL,
    sourceEvent varchar(50) NOT NULL,
    sourceUrls varchar(2000),
    treatment varchar(500) NOT NULL,
    sourceTreatmentApproach varchar(500),
    treatmentApproach varchar(500),
    applicableCancerType varchar(100) NOT NULL,
    applicableDoid varchar(50) NOT NULL,
    blacklistCancerTypes varchar(500),
    level varchar(50) NOT NULL,
    direction varchar(50) NOT NULL,
    evidenceUrls varchar(1000),
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS actionableExon;
CREATE TABLE actionableExon
(   id int NOT NULL AUTO_INCREMENT,
    modified DATETIME NOT NULL,
    gene varchar(50) NOT NULL,
    chromosome varchar(50) NOT NULL,
    start int NOT NULL,
    end int NOT NULL,
    applicableMutationType varchar(50) NOT NULL,
    source varchar(50) NOT NULL,
    sourceEvent varchar(50) NOT NULL,
    sourceUrls varchar(2000),
    treatment varchar(500) NOT NULL,
    sourceTreatmentApproach varchar(500),
    treatmentApproach varchar(500),
    applicableCancerType varchar(100) NOT NULL,
    applicableDoid varchar(50) NOT NULL,
    blacklistCancerTypes varchar(500),
    level varchar(50) NOT NULL,
    direction varchar(50) NOT NULL,
    evidenceUrls varchar(1000),
    PRIMARY KEY (id)
);