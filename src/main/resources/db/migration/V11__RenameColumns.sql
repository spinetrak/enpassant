ALTER TABLE dsb_organization
  RENAME COLUMN zps TO id;
ALTER TABLE dsb_organization
  RENAME COLUMN parent TO parentId;
ALTER TABLE dsb_player
  RENAME COLUMN zps TO clubId;
ALTER TABLE dsb_player
  RENAME COLUMN member TO memberId;
ALTER TABLE dwz
  RENAME COLUMN zps TO clubId;
ALTER TABLE dwz
  RENAME COLUMN member TO memberId;
