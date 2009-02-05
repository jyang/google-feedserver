-- Table: contact

-- DROP TABLE contact;

CREATE TABLE contact
(
  id serial NOT NULL,
  "lastName" character varying(64),
  "firstName" character varying(64),
  email character varying(64),
  rating integer,
  CONSTRAINT contact_pkey PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE contact OWNER TO postgres;