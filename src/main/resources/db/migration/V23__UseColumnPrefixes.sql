ALTER TABLE dsb_organization
  RENAME COLUMN id TO o_id;
ALTER TABLE dsb_organization
  RENAME COLUMN name TO o_name;
ALTER TABLE dsb_organization
  RENAME COLUMN level TO o_level;
ALTER TABLE dsb_organization
  RENAME COLUMN isclub TO o_isclub;
ALTER TABLE dsb_organization
  RENAME COLUMN parentid TO o_parentid;
ALTER TABLE dsb_player
  RENAME COLUMN clubid TO p_clubid;
ALTER TABLE dsb_player
  RENAME COLUMN memberid TO p_memberid;
ALTER TABLE dsb_player
  RENAME COLUMN dsbid TO p_dsbid;
ALTER TABLE dsb_player
  RENAME COLUMN name TO p_name;
ALTER TABLE dsb_player
  RENAME COLUMN status TO p_status;
ALTER TABLE dsb_player
  RENAME COLUMN gender TO p_gender;
ALTER TABLE dsb_player
  RENAME COLUMN yob TO p_yob;
ALTER TABLE dsb_player
  RENAME COLUMN eligibility TO p_eligibility;
ALTER TABLE dsb_player
  RENAME COLUMN fideid TO p_fideid;
ALTER TABLE dwz
  RENAME COLUMN clubid TO d_clubid;
ALTER TABLE dwz
  RENAME COLUMN memberid TO d_memberid;
ALTER TABLE dwz
  RENAME COLUMN lasteval TO d_lasteval;
ALTER TABLE dwz
  RENAME COLUMN dwz TO d_dwz;
ALTER TABLE dwz
  RENAME COLUMN index TO d_index;
ALTER TABLE fide
  RENAME COLUMN id TO f_id;
ALTER TABLE fide
  RENAME COLUMN lasteval TO f_lasteval;
ALTER TABLE fide
  RENAME COLUMN elo TO f_elo;
ALTER TABLE fide
  RENAME COLUMN title TO f_title;
ALTER TABLE fide
  RENAME COLUMN country TO f_country;













