DROP FUNCTION getMemberStatsByAgeForAssociationOrClub( CHAR(5) );

CREATE FUNCTION getMemberStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(avg BIGINT, yob SMALLINT) AS
$$
SELECT
  count(*) AS members,
  p.yob
FROM dsb_player p

WHERE p.clubid IN (
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
GROUP BY p.yob
ORDER BY p.yob DESC;
$$
LANGUAGE SQL;
