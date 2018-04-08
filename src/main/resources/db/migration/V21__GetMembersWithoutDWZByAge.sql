CREATE FUNCTION getMembersWithoutDWZByAge(IN CHAR(5))
  RETURNS TABLE(members BIGINT, yob SMALLINT) AS
$$
SELECT
  count(*) AS members,
  p.yob
FROM dsb_player p
  LEFT JOIN dwz d ON p.memberid = d.memberid AND p.clubid = d.clubid
WHERE
  d.memberid IS NULL AND d.clubid IS NULL AND

  p.clubid IN (
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
