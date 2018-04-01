CREATE FUNCTION getELOStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg NUMERIC, yob SMALLINT) AS
$$
SELECT
  avg(f1.elo),
  p.yob
FROM dsb_player p
  JOIN fide f1 ON (p.fideid = f1.id)
  LEFT OUTER JOIN fide f2 ON (p.fideid = f2.id
                              AND (f1.lasteval < f2.lasteval))
WHERE f2.id IS NULL
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
      AND p.fideid = f1.id
GROUP BY p.yob
ORDER BY p.yob DESC;
$$
LANGUAGE SQL;
