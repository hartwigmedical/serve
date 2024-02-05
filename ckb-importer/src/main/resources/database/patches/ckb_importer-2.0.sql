ALTER TABLE variant
    ADD COLUMN hotspotReference varchar(50) after polymorphism;

ALTER TABLE variant
    ADD COLUMN isHotspot tinyint(1) NOT NULL after hotspotReference;