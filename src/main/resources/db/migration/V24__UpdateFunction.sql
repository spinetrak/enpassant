DROP FUNCTION getMemberStatsByAgeForAssociationOrClub( CHAR(5) );

CREATE FUNCTION getMemberStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg BIGINT, yob SMALLINT) AS
$$
SELECT
  count(*) AS members,
  p.p_yob  AS yob
FROM dsb_player p

WHERE p.p_clubid IN (
  SELECT p_clubid
  FROM dsb_player
  WHERE p_clubid IN (
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
    ORDER BY o_id
  )
)
GROUP BY p.p_yob
ORDER BY p.p_yob DESC;
$$
LANGUAGE SQL;


DROP FUNCTION getELOStatsByAgeForAssociationOrClub( CHAR(5) );

CREATE FUNCTION getELOStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg NUMERIC, yob SMALLINT) AS
$$
SELECT
  avg(f1.f_elo),
  p.p_yob AS yob
FROM dsb_player p
  JOIN fide f1 ON (p.p_fideid = f1.f_id)
  LEFT OUTER JOIN fide f2 ON (p.p_fideid = f2.f_id
                              AND (f1.f_lasteval < f2.f_lasteval))
WHERE f2.f_id IS NULL
      AND p.p_clubid IN (
  SELECT p_clubid
  FROM dsb_player
  WHERE p_clubid IN (
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
    ORDER BY o_id
  )
)
      AND p.p_fideid = f1.f_id
GROUP BY p.p_yob
ORDER BY p.p_yob DESC;
$$
LANGUAGE SQL;


DROP FUNCTION getDWZStatsByAgeForAssociationOrClub( CHAR(5) );

CREATE FUNCTION getDWZStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg NUMERIC, yob SMALLINT) AS $$
SELECT
  avg(d1.d_dwz),
  p.p_yob AS yob
FROM dsb_player p
  JOIN dwz d1 ON (p.p_clubid = d1.d_clubid AND p.p_memberid = d1.d_memberid)
  LEFT OUTER JOIN dwz d2 ON (p.p_clubid = d2.d_clubid AND p.p_memberid = d2.d_memberid
                             AND (d1.d_lasteval < d2.d_lasteval))
WHERE d2.d_clubid IS NULL
      AND p.p_clubid IN (
  SELECT p_clubid
  FROM dsb_player
  WHERE p_clubid IN (
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
    ORDER BY o_id
  )
)
      AND p.p_clubid = d1.d_clubid
      AND p.p_memberid = d1.d_memberid
GROUP BY p.p_yob
ORDER BY p.p_yob DESC;
$$
LANGUAGE SQL;


DROP FUNCTION getMembersWithoutDWZByAge( CHAR(5) );
CREATE FUNCTION getMembersWithoutDWZByAge(IN CHAR(5))
  RETURNS TABLE(members BIGINT, yob SMALLINT) AS
$$
SELECT
  count(*) AS members,
  p.p_yob  AS yob
FROM dsb_player p
  LEFT JOIN dwz d ON p.p_memberid = d.d_memberid AND p.p_clubid = d.d_clubid
WHERE
  d.d_memberid IS NULL AND d.d_clubid IS NULL AND

  p.p_clubid IN (
    SELECT p_clubid
    FROM dsb_player
    WHERE p_clubid IN (
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
      ORDER BY o_id
    )
  )

GROUP BY p.p_yob
ORDER BY p.p_yob DESC;
$$
LANGUAGE SQL;

DROP FUNCTION getMembersWithoutELOByAge( CHAR(5) );
CREATE FUNCTION getMembersWithoutELOByAge(IN CHAR(5))
  RETURNS TABLE(members BIGINT, yob SMALLINT) AS
$$
SELECT
  count(*) AS members,
  p.p_yob  AS yob
FROM dsb_player p
WHERE p.p_fideid <= 0 AND p.p_clubid IN (
  SELECT p_clubid
  FROM dsb_player
  WHERE p_clubid IN (
    WITH RECURSIVE rec (o_id) AS
    ( SELECT o.o_id
      FROM dsb_organization AS o
      WHERE o_id = $1
      UNION ALL
      SELECT o.o_id
      FROM rec, dsb_organization AS o
      WHERE o.o_parentid = rec.o_id)
    SELECT *
    FROM rec
    ORDER BY o_id
  )
)

GROUP BY p.p_yob
ORDER BY p.p_yob DESC;
$$
LANGUAGE SQL;






