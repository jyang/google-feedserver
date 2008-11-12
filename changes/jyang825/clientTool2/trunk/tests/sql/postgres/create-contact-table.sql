CREATE TABLE "Contact"
(
  id serial NOT NULL,
  "lastName" character varying(64),
  "firstName" character varying(64),
  email character varying(64),
  rating integer,
  CONSTRAINT "Contact_pkey" PRIMARY KEY (id)
)
WITHOUT OIDS;

CREATE INDEX "firstName_index" ON "Contact" USING btree ("firstName");
CREATE INDEX "lastName_index" ON "Contact" USING btree ("lastName");
CREATE INDEX "rating_index" ON "Contact" USING btree ("rating");
