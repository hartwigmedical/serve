CREATE OR REPLACE VIEW actionableTrials
AS (
SELECT nctId, group_concat(DISTINCT sourceEvent ORDER BY sourceEvent SEPARATOR ', ') AS sourceEvents, title, acronym, countriesAndCities, hospitalsPerCity, therapyNames, genderCriterium, indications, urls
    FROM actionableTrial
    INNER JOIN trialMolecularCriterium ON actionableTrial.id=trialMolecularCriterium.actionableTrialId
    INNER JOIN (
        SELECT molecularCriteriumId, sourceEvent FROM actionableCharacteristic
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableCodon
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableExon
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId from trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableFusion
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableGene
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableHla
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
            UNION
        SELECT molecularCriteriumId, sourceEvent FROM actionableHotspot
        WHERE molecularCriteriumId IN (SELECT molecularCriteriumId FROM trialMolecularCriterium)
    ) AS result ON trialMolecularCriterium.molecularCriteriumId=result.molecularCriteriumId
GROUP BY 1
ORDER BY 2
);