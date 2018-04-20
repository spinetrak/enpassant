DROP FUNCTION getMemberStatsByAgeForAssociationOrClub( CHAR(5) );

CREATE FUNCTION getMemberStatsByAgeForAssociationOrClub(IN CHAR(5))
  RETURNS TABLE(members BIGINT, yob SMALLINT) AS
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








