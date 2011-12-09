select q.id from q where q.txt='بسم';
SELECT id, txt FROM "main"."q" WHERE txt LIKE '%بسم%' AND txt LIKE '%الله%';