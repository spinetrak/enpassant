CREATE TABLE DSB_ORGANIZATION
(
  zps    CHAR(5) PRIMARY KEY NOT NULL,
  name   VARCHAR(45)         NOT NULL,
  level  SMALLINT            NOT NULL,
  isclub BOOLEAN             NOT NULL,
  parent CHAR(5)
);

CREATE TABLE DSB_PLAYER
(
  zps           CHAR(5)     NOT NULL,
  member_number CHAR(4)     NOT NULL,
  dsbid         INTEGER     NOT NULL,
  name          VARCHAR(40) NOT NULL,
  status        CHAR(1),
  gender        CHAR(1),
  yob           SMALLINT,
  eligibility   CHAR(1),
  PRIMARY KEY (zps, member_number),
  UNIQUE (dsbid)
);

CREATE TABLE FIDE
(
  fide_id      INTEGER NOT NULL,
  fide_elo     SMALLINT,
  fide_title   CHAR(2),
  fide_country CHAR(3),
  lastupdate   DATE    NOT NULL,
  PRIMARY KEY (fide_id, lastupdate)
);

CREATE TABLE DWZ
(
  zps           CHAR(5)  NOT NULL,
  member_number CHAR(4)  NOT NULL,
  dwz           SMALLINT NOT NULL,
  dwz_index     SMALLINT NOT NULL,
  lastupdate    DATE     NOT NULL,
  PRIMARY KEY (zps, member_number, lastupdate)
);