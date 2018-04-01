DROP FUNCTION getDWZStatsByAgeForAssociationOrClub;

CREATE FUNCTION getDWZStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg NUMERIC, yob SMALLINT) AS $$
SELECT
  avg(d1.dwz),
  p.yob
FROM dsb_player p
  JOIN dwz d1 ON (p.clubid = d1.clubid AND p.memberid = d1.memberid)
  LEFT OUTER JOIN dwz d2 ON (p.clubid = d2.clubid AND p.memberid = d2.memberid
                             AND (d1.lasteval < d2.lasteval))
WHERE d2.clubid IS NULL
      AND p.clubid IN (
  SELECT clubid
  FROM dsb_player
  WHERE clubid IN (
    WITH RECURSIVE rec (id) AS
    (SELECT o.id
     FROM dsb_organization AS o
     WHERE id = $1
     UNION ALL
     SELECT o.id
     FROM rec, dsb_organization AS o
     WHERE o.parentid = rec.id)
    SELECT *
    FROM rec
    ORDER BY id
  )
)
      AND p.clubid = d1.clubid
      AND p.memberid = d1.memberid
GROUP BY p.yob
ORDER BY p.yob DESC;
$$
LANGUAGE SQL;
