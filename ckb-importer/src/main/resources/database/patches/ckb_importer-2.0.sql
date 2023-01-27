ALTER TABLE variant
    ADD COLUMN hotspotReference varchar(50) after polymorphism;

ALTER TABLE variant
    ADD COLUMN isHotspot BOOLEAN NOT NULL after hotspotReference;