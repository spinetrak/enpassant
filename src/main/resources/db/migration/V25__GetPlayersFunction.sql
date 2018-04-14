CREATE FUNCTION getPlayersByOrganization(IN CHAR(5))
  RETURNS TABLE(
    p_clubid      CHAR(5),
    p_memberid    CHAR(4),
    p_dsbid       INTEGER,
    p_name        VARCHAR(60),
    p_status      CHAR(1),
    p_gender      CHAR(1),
    p_yob         SMALLINT,
    p_eligibility CHAR(1),
    p_fideid      INTEGER,
    d_memberid    CHAR(5),
    d_clubid      CHAR(4),
    d_dwz         SMALLINT,
    d_index       SMALLINT,
    d_lasteval    DATE,
    f_id          INTEGER,
    f_elo         SMALLINT,
    f_title       CHAR(3),
    f_country     CHAR(3),
    f_lasteval    DATE
  )
AS
$$
SELECT
  p.p_clubid,
  p.p_memberid,
  p.p_dsbid,
  p.p_name,
  p.p_status,
  p.p_gender,
  p.p_yob,
  p.p_eligibility,
  p.p_fideid,
  d.d_memberid,
  d.d_clubid,
  d.d_dwz,
  d.d_index,
  d.d_lasteval,
  f.f_id,
  f.f_elo,
  f.f_title,
  f.f_country,
  f.f_lasteval
FROM
  dsb_player p
  LEFT JOIN dwz d ON p.p_clubid = d.d_clubid AND p.p_memberid = d.d_memberid
  LEFT JOIN fide f ON p.p_fideid = f.f_id
WHERE p.p_clubid
      IN (
        WITH RECURSIVE rec (o_id) AS
        (SELECT o.o_id
         FROM dsb_organization AS o
         WHERE o_id = $1
         UNION ALL
         SELECT o.o_id
         FROM rec, dsb_organization AS o
         WHERE o.o_parentid = rec.o_id)
        SELECT *
        FROM rec
        ORDER BY o_id)
ORDER BY p.p_clubid, p.p_memberid, d.d_index, f.f_lasteval
$$
LANGUAGE SQL;






