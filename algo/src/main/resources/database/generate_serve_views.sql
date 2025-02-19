CREATE OR REPLACE VIEW actionableTrials
AS (
SELECT nctId, group_concat(DISTINCT sourceEvent ORDER BY sourceEvent SEPARATOR ', ') AS sourceEvents, title, acronym, countriesAndCities, hospitalsPerCity, therapyNames, genderCriterium, indications, urls
    FROM actionableTrial
    INNER JOIN trialMolecularCriterium ON actionableTrial.id=trialMolecularCriterium.actionableTrialId
    INNER JOIN (
        SELECT molecularCriteriumId, sourceEvent FROM actionableCharacteristic
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableCodon
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableExon
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableFusion
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableGene
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableHla
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableHotspot
    ) AS result ON trialMolecularCriterium.molecularCriteriumId=result.molecularCriteriumId
GROUP BY 1
ORDER BY 2
);